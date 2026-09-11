package com.collegefinder.service;

import com.collegefinder.dto.request.RecommendationRequest;
import com.collegefinder.dto.request.RegisterRequest;
import com.collegefinder.dto.response.RecommendationResponse;
import com.collegefinder.entity.College;
import com.collegefinder.repository.CollegeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class RecommendationServiceImplTest {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CollegeRepository collegeRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        collegeRepository.deleteAll();
        collegeRepository.save(College.builder()
                .id(101L).collegeName("Budget Friendly College")
                .city("Coimbatore").state("Tamil Nadu").institutionType("Autonomous")
                .courses("CSE; IT").annualFeeLakh(new BigDecimal("1.2"))
                .cutoffExam("TNEA").placementRatePct(new BigDecimal("95"))
                .averagePackageLpa(new BigDecimal("7.5")).rating(new BigDecimal("4.6"))
                .hostel(true).nirfRank(67).build());

        collegeRepository.save(College.builder()
                .id(102L).collegeName("Expensive Out Of State College")
                .city("Mumbai").state("Maharashtra").institutionType("Private")
                .courses("Mechanical").annualFeeLakh(new BigDecimal("6.0"))
                .cutoffExam("MHT-CET").placementRatePct(new BigDecimal("60"))
                .averagePackageLpa(new BigDecimal("4.0")).rating(new BigDecimal("3.5"))
                .hostel(false).nirfRank(200).build());

        RegisterRequest reg = new RegisterRequest();
        reg.setFullName("Rec Test User");
        reg.setEmail("rec.test.user@example.com");
        reg.setPassword("password123");
        userId = authService.register(reg).getUser().getId();
    }

    @Test
    void recommendsBetterMatchAboveWeakerMatch() {
        RecommendationRequest req = new RecommendationRequest();
        req.setState("Tamil Nadu");
        req.setCourse("CSE");
        req.setBudgetLakh(2.0);
        req.setMinPlacementPct(80.0);
        req.setHostelRequired(true);

        List<RecommendationResponse> results = recommendationService.recommend(userId, req);

        assertFalse(results.isEmpty());
        assertEquals("Budget Friendly College", results.get(0).getCollegeName());
        assertTrue(results.get(0).getScore().compareTo(results.get(results.size() - 1).getScore()) >= 0);
    }
}
