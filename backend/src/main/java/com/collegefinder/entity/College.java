package com.collegefinder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Maps 1:1 to the columns present in the supplied real dataset
 * (college_discovery_50_real_colleges_dataset.csv / .sql).
 * No field here is invented - every column mirrors the source dataset.
 */
@Entity
@Table(name = "colleges", indexes = {
        @Index(name = "idx_college_name", columnList = "collegeName"),
        @Index(name = "idx_college_state", columnList = "state"),
        @Index(name = "idx_college_city", columnList = "city"),
        @Index(name = "idx_college_rating", columnList = "rating"),
        @Index(name = "idx_college_fees", columnList = "annualFeeLakh"),
        @Index(name = "idx_college_placement", columnList = "placementRatePct")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class College {

    @Id
    private Long id; // preserved from source dataset (source_id)

    @Column(nullable = false, length = 255)
    private String collegeName;

    @Column(nullable = false, length = 120)
    private String city;

    @Column(nullable = false, length = 120)
    private String state;

    @Column(length = 100)
    private String institutionType; // e.g. IIT, NIT, Private/Deemed, Autonomous, State University

    @Column(columnDefinition = "TEXT")
    private String courses; // semicolon separated, as supplied in source

    private BigDecimal annualFeeLakh; // annual_fee_lakh_approx

    @Column(length = 150)
    private String cutoffExam;

    @Column(columnDefinition = "TEXT")
    private String cutoffNote;

    private BigDecimal placementRatePct;

    private BigDecimal averagePackageLpa;

    @Column(length = 50)
    private String highestPackageApprox; // kept as text - source uses mixed units ("1.31 Cr", "55 LPA+")

    private BigDecimal rating;

    private Boolean hostel;

    @Column(length = 255)
    private String website;

    private Integer nirfRank;

    @Column(columnDefinition = "TEXT")
    private String dataStatus;

    private Integer dataReferenceYear;

    // Approximate CITY-level coordinates for map display only (public geographic
    // data, resolved from city name - NOT campus-exact and NOT part of the
    // source academic/financial dataset).
    private Double latitude;
    private Double longitude;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
