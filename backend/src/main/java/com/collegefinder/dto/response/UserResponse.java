package com.collegefinder.dto.response;

import com.collegefinder.entity.Role;
import com.collegefinder.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
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
    private Role role;

    public static UserResponse fromEntity(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .state(u.getState())
                .city(u.getCity())
                .academicPercentage(u.getAcademicPercentage())
                .entranceExam(u.getEntranceExam())
                .entranceScore(u.getEntranceScore())
                .preferredCourse(u.getPreferredCourse())
                .preferredBranch(u.getPreferredBranch())
                .budgetLakh(u.getBudgetLakh())
                .hostelRequired(u.getHostelRequired())
                .role(u.getRole())
                .build();
    }
}
