package com.collegefinder.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * All fields optional - used to build a dynamic JPA Specification.
 */
@Data
public class CollegeFilterRequest {
    private String q; // free text: college name / city / state
    private String state;
    private String city;
    private String course;
    private String institutionType;
    private BigDecimal minRating;
    private BigDecimal maxFeeLakh;
    private BigDecimal minPlacementPct;
    private Boolean hostel;
    private String cutoffExam;

    private int page = 0;
    private int size = 12;
    private String sortBy = "nirfRank"; // rating | annualFeeLakh | placementRatePct | averagePackageLpa | nirfRank | collegeName
    private String sortDir = "asc";
}
