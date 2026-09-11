package com.collegefinder.service.impl;

import com.collegefinder.dto.request.RecommendationRequest;
import com.collegefinder.dto.response.CollegeResponse;
import com.collegefinder.dto.response.RecommendationResponse;
import com.collegefinder.entity.College;
import com.collegefinder.entity.Recommendation;
import com.collegefinder.entity.User;
import com.collegefinder.exception.UserNotFoundException;
import com.collegefinder.repository.CollegeRepository;
import com.collegefinder.repository.RecommendationRepository;
import com.collegefinder.repository.UserRepository;
import com.collegefinder.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scoring engine adapted to the fields actually present in the real dataset
 * (no branch-level or seat-level data is available, so the original
 * course(20)/branch(15)/facilities(5) split from the spec is consolidated
 * into the weights below - every input is a real column, nothing invented):
 *
 *   Course match            25%
 *   Location match (state/city) 15%
 *   Budget fit               20%
 *   Cutoff/difficulty fit    15%   (uses NIRF rank + institution type as a difficulty proxy)
 *   Placement rate           15%
 *   Rating                   5%
 *   Hostel                   5%
 */
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;

    @Override
    @Transactional
    public List<RecommendationResponse> recommend(Long userId, RecommendationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<College> colleges = collegeRepository.findAll();

        List<RecommendationResponse> results = colleges.stream()
                .map(college -> score(college, request))
                .sorted(Comparator.comparing(RecommendationResponse::getScore).reversed())
                .limit(20)
                .collect(Collectors.toList());

        recommendationRepository.deleteByUserId(userId);
        List<Recommendation> toSave = new ArrayList<>();
        for (RecommendationResponse r : results) {
            College college = colleges.stream().filter(c -> c.getId().equals(r.getCollegeId())).findFirst().orElse(null);
            if (college == null) continue;
            toSave.add(Recommendation.builder()
                    .user(user)
                    .college(college)
                    .score(r.getScore())
                    .matchedCriteria(r.getMatchedCriteria())
                    .unmatchedCriteria(r.getUnmatchedCriteria())
                    .reason(r.getReason())
                    .build());
        }
        recommendationRepository.saveAll(toSave);

        return results;
    }

    @Override
    public List<RecommendationResponse> getSavedRecommendations(Long userId) {
        return recommendationRepository.findByUserIdOrderByScoreDesc(userId).stream()
                .map(rec -> RecommendationResponse.builder()
                        .collegeId(rec.getCollege().getId())
                        .collegeName(rec.getCollege().getCollegeName())
                        .score(rec.getScore())
                        .matchedCriteria(rec.getMatchedCriteria())
                        .unmatchedCriteria(rec.getUnmatchedCriteria())
                        .reason(rec.getReason())
                        .college(CollegeResponse.fromEntity(rec.getCollege()))
                        .build())
                .collect(Collectors.toList());
    }

    private RecommendationResponse score(College college, RecommendationRequest request) {
        List<String> matched = new ArrayList<>();
        List<String> unmatched = new ArrayList<>();
        double total = 0;

        // Course match - 25
        if (request.getCourse() != null && !request.getCourse().isBlank()) {
            boolean match = college.getCourses() != null &&
                    college.getCourses().toLowerCase().contains(request.getCourse().toLowerCase());
            if (match) { total += 25; matched.add("preferred course (" + request.getCourse() + ")"); }
            else unmatched.add("preferred course (" + request.getCourse() + ")");
        } else {
            total += 25; // no preference stated -> treated as neutral match
        }

        // Location match - 15
        if (request.getState() != null && !request.getState().isBlank()) {
            if (college.getState().equalsIgnoreCase(request.getState())) {
                double bonus = 15;
                if (request.getCity() != null && !request.getCity().isBlank()
                        && !college.getCity().equalsIgnoreCase(request.getCity())) {
                    bonus = 10; // right state, different city
                }
                total += bonus;
                matched.add("location (" + college.getState() + ")");
            } else {
                unmatched.add("preferred state (" + request.getState() + ")");
            }
        } else {
            total += 15;
        }

        // Budget fit - 20
        if (request.getBudgetLakh() != null && college.getAnnualFeeLakh() != null) {
            double fee = college.getAnnualFeeLakh().doubleValue();
            if (fee <= request.getBudgetLakh()) {
                total += 20;
                matched.add("within budget (₹" + college.getAnnualFeeLakh() + "L/yr)");
            } else {
                double overBy = (fee - request.getBudgetLakh()) / request.getBudgetLakh();
                if (overBy <= 0.25) {
                    total += 10; // close to budget
                    matched.add("close to budget (₹" + college.getAnnualFeeLakh() + "L/yr)");
                } else {
                    unmatched.add("budget (fee is ₹" + college.getAnnualFeeLakh() + "L/yr)");
                }
            }
        } else {
            total += 20;
        }

        // Cutoff / difficulty fit - 15 (uses NIRF rank as a real-data proxy for competitiveness)
        if (request.getEntranceScorePercentile() != null && college.getNirfRank() != null) {
            double percentile = request.getEntranceScorePercentile();
            double difficulty = Math.max(0, 100 - college.getNirfRank()); // lower rank number = harder to get into
            boolean plausible = percentile >= (difficulty * 0.6);
            if (plausible) {
                total += 15;
                matched.add("cutoff compatibility (NIRF rank #" + college.getNirfRank() + ")");
            } else {
                total += 5;
                unmatched.add("cutoff may be a stretch (NIRF rank #" + college.getNirfRank() + ")");
            }
        } else {
            total += 15;
        }

        // Placement - 15
        if (request.getMinPlacementPct() != null && college.getPlacementRatePct() != null) {
            if (college.getPlacementRatePct().doubleValue() >= request.getMinPlacementPct()) {
                total += 15;
                matched.add("placement rate (" + college.getPlacementRatePct() + "%)");
            } else {
                unmatched.add("placement rate below target (" + college.getPlacementRatePct() + "%)");
            }
        } else {
            total += 15;
        }

        // Rating - 5
        if (request.getMinRating() != null && college.getRating() != null) {
            if (college.getRating().doubleValue() >= request.getMinRating()) {
                total += 5;
                matched.add("rating (" + college.getRating() + "/5)");
            } else {
                unmatched.add("rating below target (" + college.getRating() + "/5)");
            }
        } else {
            total += 5;
        }

        // Hostel - 5
        if (Boolean.TRUE.equals(request.getHostelRequired())) {
            if (Boolean.TRUE.equals(college.getHostel())) {
                total += 5;
                matched.add("hostel available");
            } else {
                unmatched.add("hostel not available");
            }
        } else {
            total += 5;
        }

        BigDecimal score = BigDecimal.valueOf(total).setScale(1, RoundingMode.HALF_UP);
        String reason = "Matches " + (matched.isEmpty() ? "few of your preferences" : String.join(", ", matched)) + "."
                + (unmatched.isEmpty() ? "" : " Does not fully match " + String.join(", ", unmatched) + ".");

        return RecommendationResponse.builder()
                .collegeId(college.getId())
                .collegeName(college.getCollegeName())
                .score(score)
                .matchedCriteria(String.join(", ", matched))
                .unmatchedCriteria(String.join(", ", unmatched))
                .reason(reason)
                .college(CollegeResponse.fromEntity(college))
                .build();
    }
}
