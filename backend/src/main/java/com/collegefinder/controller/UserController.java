package com.collegefinder.controller;

import com.collegefinder.dto.request.ProfileUpdateRequest;
import com.collegefinder.dto.response.UserResponse;
import com.collegefinder.entity.User;
import com.collegefinder.exception.UserNotFoundException;
import com.collegefinder.repository.UserRepository;
import com.collegefinder.security.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public UserResponse getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return UserResponse.fromEntity(principal.getUser());
    }

    @PutMapping("/profile")
    @Transactional
    public UserResponse updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                       @RequestBody ProfileUpdateRequest request) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getAcademicPercentage() != null) user.setAcademicPercentage(request.getAcademicPercentage());
        if (request.getEntranceExam() != null) user.setEntranceExam(request.getEntranceExam());
        if (request.getEntranceScore() != null) user.setEntranceScore(request.getEntranceScore());
        if (request.getPreferredCourse() != null) user.setPreferredCourse(request.getPreferredCourse());
        if (request.getPreferredBranch() != null) user.setPreferredBranch(request.getPreferredBranch());
        if (request.getBudgetLakh() != null) user.setBudgetLakh(request.getBudgetLakh());
        if (request.getHostelRequired() != null) user.setHostelRequired(request.getHostelRequired());

        return UserResponse.fromEntity(userRepository.save(user));
    }
}
