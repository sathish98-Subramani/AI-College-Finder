package com.collegefinder.service;

import com.collegefinder.entity.SearchHistory;

import java.util.List;

public interface SearchHistoryService {
    void record(Long userId, String query, String filtersJson);
    List<SearchHistory> recent(Long userId, int limit);
}
