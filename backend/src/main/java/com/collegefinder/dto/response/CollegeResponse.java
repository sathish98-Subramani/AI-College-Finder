package com.collegefinder.dto.response;

import com.collegefinder.entity.College;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollegeResponse {
    private Long id;
    private String collegeName;
    private String city;
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
    private Double latitude;
    private Double longitude;

    public static CollegeResponse fromEntity(College c) {
        return CollegeResponse.builder()
                .id(c.getId())
                .collegeName(c.getCollegeName())
                .city(c.getCity())
                .state(c.getState())
                .institutionType(c.getInstitutionType())
                .courses(c.getCourses())
                .annualFeeLakh(c.getAnnualFeeLakh())
                .cutoffExam(c.getCutoffExam())
                .cutoffNote(c.getCutoffNote())
                .placementRatePct(c.getPlacementRatePct())
                .averagePackageLpa(c.getAveragePackageLpa())
                .highestPackageApprox(c.getHighestPackageApprox())
                .rating(c.getRating())
                .hostel(c.getHostel())
                .website(c.getWebsite())
                .nirfRank(c.getNirfRank())
                .dataStatus(c.getDataStatus())
                .dataReferenceYear(c.getDataReferenceYear())
                .latitude(c.getLatitude())
                .longitude(c.getLongitude())
                .build();
    }
}
