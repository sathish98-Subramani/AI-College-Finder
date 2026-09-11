package com.collegefinder.controller;

import com.collegefinder.dto.request.RecommendationRequest;
import com.collegefinder.dto.response.RecommendationResponse;
import com.collegefinder.security.UserPrincipal;
import com.collegefinder.service.RecommendationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public List<RecommendationResponse> recommend(@AuthenticationPrincipal UserPrincipal principal,
                                                    @RequestBody RecommendationRequest request) {
        return recommendationService.recommend(principal.getId(), request);
    }

    @GetMapping
    public List<RecommendationResponse> mine(@AuthenticationPrincipal UserPrincipal principal) {
        return recommendationService.getSavedRecommendations(principal.getId());
    }

    @GetMapping("/{userId}")
    public List<RecommendationResponse> forUser(@PathVariable Long userId) {
        return recommendationService.getSavedRecommendations(userId);
    }
}
