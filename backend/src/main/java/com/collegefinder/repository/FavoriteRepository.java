package com.collegefinder.repository;

import com.collegefinder.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndCollegeId(Long userId, Long collegeId);
    boolean existsByUserIdAndCollegeId(Long userId, Long collegeId);
    void deleteByUserIdAndCollegeId(Long userId, Long collegeId);
}
