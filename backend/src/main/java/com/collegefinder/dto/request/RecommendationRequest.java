package com.collegefinder.dto.request;

import lombok.Data;

@Data
public class RecommendationRequest {
    private String state;
    private String city;
    private String course;
    private Double budgetLakh;
    private Double entranceScorePercentile; // 0-100, used against a simplified cutoff-difficulty proxy
    private Double minPlacementPct;
    private Double minRating;
    private Boolean hostelRequired;
    private String institutionType;
}
