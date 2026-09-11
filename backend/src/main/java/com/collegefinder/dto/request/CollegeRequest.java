package com.collegefinder.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CollegeRequest {
    @NotBlank
    private String collegeName;
    @NotBlank
    private String city;
    @NotBlank
    private String state;
    private String institutionType;
    private String courses;
    private BigDecimal annualFeeLakh;
    private String cutoffExam;
    private String cutoffNote;
    private BigDecimal placementRatePct;
    private BigDecimal averagePackageLpa;
    private String highestPackageApprox;
    private BigDecimal rating;
    private Boolean hostel;
    private String website;
    private Integer nirfRank;
    private String dataStatus;
    private Integer dataReferenceYear;
}
