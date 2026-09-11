package com.collegefinder.controller;

import com.collegefinder.dto.response.ImportSummaryResponse;
import com.collegefinder.dto.response.UserResponse;
import com.collegefinder.entity.College;
import com.collegefinder.entity.Role;
import com.collegefinder.repository.CollegeRepository;
import com.collegefinder.repository.RecommendationRepository;
import com.collegefinder.repository.UserRepository;
import com.collegefinder.service.CollegeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final CollegeService collegeService;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;

    @PostMapping("/colleges/import")
    public ImportSummaryResponse importColleges(@RequestParam("file") MultipartFile file) {
        return collegeService.importCsv(file);
    }

    @GetMapping("/students")
    public List<UserResponse> students() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.STUDENT)
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<College> colleges = collegeRepository.findAll();
        long totalColleges = colleges.size();
        long totalStudents = userRepository.findAll().stream().filter(u -> u.getRole() == Role.STUDENT).count();
        long totalRecommendationsGenerated = recommendationRepository.count();

        Map<String, Long> byState = colleges.stream()
                .collect(Collectors.groupingBy(College::getState, Collectors.counting()));
        Map<String, Long> byCity = colleges.stream()
                .collect(Collectors.groupingBy(College::getCity, Collectors.counting()));
        Map<String, Long> byType = colleges.stream()
                .filter(c -> c.getInstitutionType() != null)
                .collect(Collectors.groupingBy(College::getInstitutionType, Collectors.counting()));

        BigDecimal avgFee = average(colleges.stream().map(College::getAnnualFeeLakh));
        BigDecimal avgRating = average(colleges.stream().map(College::getRating));
        BigDecimal avgPlacement = average(colleges.stream().map(College::getPlacementRatePct));

        return Map.of(
                "totalColleges", totalColleges,
                "totalStudents", totalStudents,
                "totalRecommendationsGenerated", totalRecommendationsGenerated,
                "collegesByState", byState,
                "collegesByCity", byCity,
                "collegesByType", byType,
                "averageAnnualFeeLakh", avgFee,
                "averageRating", avgRating,
                "averagePlacementRatePct", avgPlacement
        );
    }

    private BigDecimal average(java.util.stream.Stream<BigDecimal> values) {
        List<BigDecimal> list = values.filter(v -> v != null).collect(Collectors.toList());
        if (list.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = list.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
    }
}
