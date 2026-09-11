package com.collegefinder.config;

import com.collegefinder.entity.College;
import com.collegefinder.repository.CollegeRepository;
import com.collegefinder.util.CityCoordinates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads /resources/data/college_dataset.csv (the real 50-college dataset
 * supplied by the project owner) into the database on first startup, only
 * if the colleges table is currently empty. Safe to re-run - it is a no-op
 * once data exists. Admins can still use POST /api/admin/colleges/import
 * to (re)import or update the dataset later.
 */
@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final CollegeRepository collegeRepository;

    @Override
    public void run(String... args) {
        if (collegeRepository.count() > 0) {
            log.info("College table already has {} records - skipping seed import", collegeRepository.count());
            return;
        }

        try (Reader reader = new InputStreamReader(
                new ClassPathResource("data/college_dataset.csv").getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader().setSkipHeaderRecord(true).setTrim(true).setIgnoreSurroundingSpaces(true)
                     .build().parse(reader)) {

            List<College> colleges = new ArrayList<>();
            for (CSVRecord r : parser) {
                College college = College.builder()
                        .id(Long.parseLong(r.get("id")))
                        .collegeName(r.get("college_name"))
                        .city(r.get("city"))
                        .state(r.get("state"))
                        .institutionType(r.get("institution_type"))
                        .courses(r.get("courses"))
                        .annualFeeLakh(parseDecimal(r.get("annual_fee_lakh_approx")))
                        .cutoffExam(r.get("cutoff_exam"))
                        .cutoffNote(r.get("cutoff_note"))
                        .placementRatePct(parseDecimal(r.get("placement_rate_pct_approx")))
                        .averagePackageLpa(parseDecimal(r.get("average_package_lpa_approx")))
                        .highestPackageApprox(r.get("highest_package_approx"))
                        .rating(parseDecimal(r.get("rating_demo_out_of_5")))
                        .hostel("Yes".equalsIgnoreCase(r.get("hostel")))
                        .website(r.get("website"))
                        .nirfRank(parseInt(r.get("nirf_engineering_rank_2025")))
                        .dataStatus(r.get("data_status"))
                        .dataReferenceYear(parseInt(r.get("data_reference_year")))
                        .build();

                double[] coords = CityCoordinates.lookup(college.getCity());
                if (coords != null) {
                    college.setLatitude(coords[0]);
                    college.setLongitude(coords[1]);
                }
                colleges.add(college);
            }

            collegeRepository.saveAll(colleges);
            log.info("Seeded {} colleges from the real dataset", colleges.size());
        } catch (Exception e) {
            log.error("Failed to seed college dataset on startup: {}", e.getMessage(), e);
        }
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try { return new BigDecimal(value.trim()); } catch (NumberFormatException e) { return null; }
    }

    private Integer parseInt(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Integer.parseInt(value.trim()); } catch (NumberFormatException e) { return null; }
    }
}
