package com.collegefinder.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String fullName;
    private String phone;
    private String state;
    private String city;
    private Double academicPercentage;
    private String entranceExam;
    private Double entranceScore;
    private String preferredCourse;
    private String preferredBranch;
    private Double budgetLakh;
    private Boolean hostelRequired;
}
