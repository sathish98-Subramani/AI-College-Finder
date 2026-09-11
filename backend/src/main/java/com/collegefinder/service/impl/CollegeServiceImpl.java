package com.collegefinder.service.impl;

import com.collegefinder.dto.request.CollegeFilterRequest;
import com.collegefinder.dto.request.CollegeRequest;
import com.collegefinder.dto.response.CollegeResponse;
import com.collegefinder.dto.response.ImportSummaryResponse;
import com.collegefinder.dto.response.PageResponse;
import com.collegefinder.entity.College;
import com.collegefinder.exception.CollegeNotFoundException;
import com.collegefinder.exception.DuplicateCollegeException;
import com.collegefinder.repository.CollegeRepository;
import com.collegefinder.service.CollegeService;
import com.collegefinder.specification.CollegeSpecification;
import com.collegefinder.util.CityCoordinates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "rating", "annualFeeLakh", "placementRatePct", "averagePackageLpa", "nirfRank", "collegeName"
    );

    @Override
    public PageResponse<CollegeResponse> search(CollegeFilterRequest filter) {
        String sortBy = ALLOWED_SORT_FIELDS.contains(filter.getSortBy()) ? filter.getSortBy() : "nirfRank";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC;

        PageRequest pageRequest = PageRequest.of(
                Math.max(filter.getPage(), 0),
                Math.min(Math.max(filter.getSize(), 1), 100),
                Sort.by(direction, sortBy)
        );

        Page<College> page = collegeRepository.findAll(CollegeSpecification.fromFilter(filter), pageRequest);
        Page<CollegeResponse> mapped = page.map(CollegeResponse::fromEntity);
        return PageResponse.of(mapped);
    }

    @Override
    public CollegeResponse getById(Long id) {
        return CollegeResponse.fromEntity(findEntity(id));
    }

    @Override
    public List<CollegeResponse> similarColleges(Long id, int limit) {
        College college = findEntity(id);
        return collegeRepository.findAll().stream()
                .filter(c -> !c.getId().equals(id))
                .filter(c -> c.getState().equalsIgnoreCase(college.getState())
                        || overlappingCourses(c.getCourses(), college.getCourses()))
                .sorted(Comparator.comparing((College c) -> Math.abs(
                                safe(c.getNirfRank()) - safe(college.getNirfRank())))
                        .thenComparing(c -> c.getRating() == null ? BigDecimal.ZERO : c.getRating(), Comparator.reverseOrder()))
                .limit(limit)
                .map(CollegeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private int safe(Integer i) {
        return i == null ? Integer.MAX_VALUE / 2 : i;
    }

    private boolean overlappingCourses(String a, String b) {
        if (a == null || b == null) return false;
        Set<String> setA = splitCourses(a);
        Set<String> setB = splitCourses(b);
        return setA.stream().anyMatch(setB::contains);
    }

    private Set<String> splitCourses(String courses) {
        return List.of(courses.toLowerCase().split(";")).stream()
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public CollegeResponse create(CollegeRequest request) {
        if (collegeRepository.findByCollegeNameIgnoreCase(request.getCollegeName()).isPresent()) {
            throw new DuplicateCollegeException("A college with this name already exists: " + request.getCollegeName());
        }
        Long nextId = collegeRepository.findAll().stream()
                .map(College::getId).max(Long::compareTo).orElse(0L) + 1;

        College college = mapRequestToEntity(new College(), request);
        college.setId(nextId);
        applyCityCoordinates(college);
        return CollegeResponse.fromEntity(collegeRepository.save(college));
    }

    @Override
    @Transactional
    public CollegeResponse update(Long id, CollegeRequest request) {
        College college = findEntity(id);
        mapRequestToEntity(college, request);
        applyCityCoordinates(college);
        return CollegeResponse.fromEntity(collegeRepository.save(college));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        College college = findEntity(id);
        collegeRepository.delete(college);
    }

    @Override
    @Transactional
    public List<CollegeResponse> compare(List<Long> ids) {
        if (ids == null || ids.size() < 2 || ids.size() > 4) {
            throw new IllegalArgumentException("Comparison requires between 2 and 4 college ids");
        }
        return ids.stream().map(this::findEntity).map(CollegeResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ImportSummaryResponse importCsv(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int inserted = 0, updated = 0, duplicates = 0, failed = 0, total = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .setIgnoreSurroundingSpaces(true)
                     .build()
                     .parse(reader)) {

            Set<String> seenNames = new java.util.HashSet<>();

            for (CSVRecord record : parser) {
                total++;
                try {
                    String name = get(record, "college_name");
                    if (name == null || name.isBlank()) {
                        failed++;
                        errors.add("Row " + record.getRecordNumber() + ": missing college_name");
                        continue;
                    }
                    if (!seenNames.add(name.toLowerCase())) {
                        duplicates++;
                        continue;
                    }

                    var existing = collegeRepository.findByCollegeNameIgnoreCase(name);
                    College college = existing.orElseGet(College::new);
                    boolean isNew = existing.isEmpty();

                    if (isNew) {
                        Long nextId = collegeRepository.findAll().stream()
                                .map(College::getId).max(Long::compareTo).orElse(0L) + 1;
                        college.setId(nextId);
                    }

                    college.setCollegeName(name);
                    college.setCity(get(record, "city"));
                    college.setState(get(record, "state"));
                    college.setInstitutionType(get(record, "institution_type"));
                    college.setCourses(get(record, "courses"));
                    college.setAnnualFeeLakh(parseDecimal(get(record, "annual_fee_lakh_approx")));
                    college.setCutoffExam(get(record, "cutoff_exam"));
                    college.setCutoffNote(get(record, "cutoff_note"));
                    college.setPlacementRatePct(parseDecimal(get(record, "placement_rate_pct_approx")));
                    college.setAveragePackageLpa(parseDecimal(get(record, "average_package_lpa_approx")));
                    college.setHighestPackageApprox(get(record, "highest_package_approx"));
                    college.setRating(parseDecimal(get(record, "rating_demo_out_of_5")));
                    college.setHostel("Yes".equalsIgnoreCase(get(record, "hostel")));
                    college.setWebsite(get(record, "website"));
                    college.setNirfRank(parseInt(get(record, "nirf_engineering_rank_2025")));
                    college.setDataStatus(get(record, "data_status"));
                    college.setDataReferenceYear(parseInt(get(record, "data_reference_year")));
                    applyCityCoordinates(college);

                    collegeRepository.save(college);
                    if (isNew) inserted++; else updated++;
                } catch (Exception rowEx) {
                    failed++;
                    errors.add("Row " + record.getRecordNumber() + ": " + rowEx.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file: " + e.getMessage(), e);
        }

        return ImportSummaryResponse.builder()
                .totalRecords(total)
                .inserted(inserted)
                .updated(updated)
                .duplicates(duplicates)
                .failed(failed)
                .errors(errors)
                .build();
    }

    private String get(CSVRecord record, String header) {
        return record.isMapped(header) ? record.get(header) : null;
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void applyCityCoordinates(College college) {
        double[] coords = CityCoordinates.lookup(college.getCity());
        if (coords != null) {
            college.setLatitude(coords[0]);
            college.setLongitude(coords[1]);
        }
    }

    private College mapRequestToEntity(College college, CollegeRequest request) {
        college.setCollegeName(request.getCollegeName());
        college.setCity(request.getCity());
        college.setState(request.getState());
        college.setInstitutionType(request.getInstitutionType());
        college.setCourses(request.getCourses());
        college.setAnnualFeeLakh(request.getAnnualFeeLakh());
        college.setCutoffExam(request.getCutoffExam());
        college.setCutoffNote(request.getCutoffNote());
        college.setPlacementRatePct(request.getPlacementRatePct());
        college.setAveragePackageLpa(request.getAveragePackageLpa());
        college.setHighestPackageApprox(request.getHighestPackageApprox());
        college.setRating(request.getRating());
        college.setHostel(request.getHostel());
        college.setWebsite(request.getWebsite());
        college.setNirfRank(request.getNirfRank());
        college.setDataStatus(request.getDataStatus());
        college.setDataReferenceYear(request.getDataReferenceYear());
        return college;
    }

    private College findEntity(Long id) {
        return collegeRepository.findById(id)
                .orElseThrow(() -> new CollegeNotFoundException("College not found with id: " + id));
    }
}
