package com.collegefinder.service;

import com.collegefinder.dto.response.CollegeResponse;

import java.util.List;

public interface FavoriteService {
    void addFavorite(Long userId, Long collegeId);
    void removeFavorite(Long userId, Long collegeId);
    List<CollegeResponse> getFavorites(Long userId);
}
