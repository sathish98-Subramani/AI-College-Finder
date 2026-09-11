package com.collegefinder.repository;

import com.collegefinder.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserIdOrderByScoreDesc(Long userId);
    void deleteByUserId(Long userId);
}
