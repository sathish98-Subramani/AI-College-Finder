package com.collegefinder.controller;

import com.collegefinder.dto.request.CollegeFilterRequest;
import com.collegefinder.dto.request.CollegeRequest;
import com.collegefinder.dto.request.CompareRequest;
import com.collegefinder.dto.response.CollegeResponse;
import com.collegefinder.dto.response.PageResponse;
import com.collegefinder.service.CollegeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/colleges")
@RequiredArgsConstructor
@Tag(name = "Colleges")
public class CollegeController {

    private final CollegeService collegeService;

    @GetMapping
    public PageResponse<CollegeResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String institutionType,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) BigDecimal maxFeeLakh,
            @RequestParam(required = false) BigDecimal minPlacementPct,
            @RequestParam(required = false) Boolean hostel,
            @RequestParam(required = false) String cutoffExam,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "nirfRank") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        CollegeFilterRequest filter = new CollegeFilterRequest();
        filter.setQ(q);
        filter.setState(state);
        filter.setCity(city);
        filter.setCourse(course);
        filter.setInstitutionType(institutionType);
        filter.setMinRating(minRating);
        filter.setMaxFeeLakh(maxFeeLakh);
        filter.setMinPlacementPct(minPlacementPct);
        filter.setHostel(hostel);
        filter.setCutoffExam(cutoffExam);
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDir(sortDir);
        return collegeService.search(filter);
    }

    // Alias kept for spec compatibility - identical to GET /api/colleges
    @GetMapping("/search")
    public PageResponse<CollegeResponse> searchAlias(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        CollegeFilterRequest filter = new CollegeFilterRequest();
        filter.setQ(q);
        filter.setPage(page);
        filter.setSize(size);
        return collegeService.search(filter);
    }

    // Alias kept for spec compatibility - identical to GET /api/colleges
    @GetMapping("/filter")
    public PageResponse<CollegeResponse> filterAlias(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String course,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        CollegeFilterRequest filter = new CollegeFilterRequest();
        filter.setState(state);
        filter.setCity(city);
        filter.setCourse(course);
        filter.setPage(page);
        filter.setSize(size);
        return collegeService.search(filter);
    }

    @GetMapping("/{id}")
    public CollegeResponse getById(@PathVariable Long id) {
        return collegeService.getById(id);
    }

    @GetMapping("/{id}/similar")
    public List<CollegeResponse> similar(@PathVariable Long id, @RequestParam(defaultValue = "4") int limit) {
        return collegeService.similarColleges(id, limit);
    }

    @PostMapping
    public ResponseEntity<CollegeResponse> create(@Valid @RequestBody CollegeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collegeService.create(request));
    }

    @PutMapping("/{id}")
    public CollegeResponse update(@PathVariable Long id, @Valid @RequestBody CollegeRequest request) {
        return collegeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        collegeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
