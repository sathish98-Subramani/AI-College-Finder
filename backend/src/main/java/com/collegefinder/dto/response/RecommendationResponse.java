package com.collegefinder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private Long collegeId;
    private String collegeName;
    private BigDecimal score;
    private String matchedCriteria;
    private String unmatchedCriteria;
    private String reason;
    private CollegeResponse college;
}
