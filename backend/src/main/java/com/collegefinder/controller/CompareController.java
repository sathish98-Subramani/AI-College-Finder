package com.collegefinder.controller;

import com.collegefinder.dto.request.CompareRequest;
import com.collegefinder.dto.response.CollegeResponse;
import com.collegefinder.service.CollegeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/compare")
@RequiredArgsConstructor
@Tag(name = "Comparison")
public class CompareController {

    private final CollegeService collegeService;

    @PostMapping
    public List<CollegeResponse> compare(@RequestBody CompareRequest request) {
        return collegeService.compare(request.getCollegeIds());
    }
}
