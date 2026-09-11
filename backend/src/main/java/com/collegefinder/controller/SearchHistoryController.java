package com.collegefinder.controller;

import com.collegefinder.entity.SearchHistory;
import com.collegefinder.security.UserPrincipal;
import com.collegefinder.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
@Tag(name = "Search History")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping
    public List<Map<String, Object>> recent(@AuthenticationPrincipal UserPrincipal principal,
                                             @RequestParam(defaultValue = "10") int limit) {
        return searchHistoryService.recent(principal.getId(), limit).stream()
                .map(h -> Map.<String, Object>of(
                        "id", h.getId(),
                        "query", h.getSearchQuery() == null ? "" : h.getSearchQuery(),
                        "filters", h.getFiltersJson() == null ? "" : h.getFiltersJson(),
                        "createdAt", h.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
