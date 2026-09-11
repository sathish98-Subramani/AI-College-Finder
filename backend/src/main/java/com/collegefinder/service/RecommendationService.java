package com.collegefinder.service;

import com.collegefinder.dto.request.RecommendationRequest;
import com.collegefinder.dto.response.RecommendationResponse;

import java.util.List;

public interface RecommendationService {
    List<RecommendationResponse> recommend(Long userId, RecommendationRequest request);
    List<RecommendationResponse> getSavedRecommendations(Long userId);
}
