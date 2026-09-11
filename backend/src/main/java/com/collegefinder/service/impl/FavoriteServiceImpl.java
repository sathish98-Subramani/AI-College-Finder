package com.collegefinder.service.impl;

import com.collegefinder.dto.response.CollegeResponse;
import com.collegefinder.entity.College;
import com.collegefinder.entity.Favorite;
import com.collegefinder.entity.User;
import com.collegefinder.exception.CollegeNotFoundException;
import com.collegefinder.exception.UserNotFoundException;
import com.collegefinder.repository.CollegeRepository;
import com.collegefinder.repository.FavoriteRepository;
import com.collegefinder.repository.UserRepository;
import com.collegefinder.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long collegeId) {
        if (favoriteRepository.existsByUserIdAndCollegeId(userId, collegeId)) {
            return;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new CollegeNotFoundException("College not found with id: " + collegeId));

        favoriteRepository.save(Favorite.builder().user(user).college(college).build());
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long collegeId) {
        favoriteRepository.deleteByUserIdAndCollegeId(userId, collegeId);
    }

    @Override
    public List<CollegeResponse> getFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(f -> CollegeResponse.fromEntity(f.getCollege()))
                .collect(Collectors.toList());
    }
}
