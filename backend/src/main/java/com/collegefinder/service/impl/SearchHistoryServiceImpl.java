package com.collegefinder.service.impl;

import com.collegefinder.entity.SearchHistory;
import com.collegefinder.entity.User;
import com.collegefinder.exception.UserNotFoundException;
import com.collegefinder.repository.SearchHistoryRepository;
import com.collegefinder.repository.UserRepository;
import com.collegefinder.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryServiceImpl implements SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void record(Long userId, String query, String filtersJson) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        searchHistoryRepository.save(SearchHistory.builder()
                .user(user)
                .searchQuery(query)
                .filtersJson(filtersJson)
                .build());
    }

    @Override
    public List<SearchHistory> recent(Long userId, int limit) {
        return searchHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit));
    }
}
