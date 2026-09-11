package com.collegefinder.specification;

import com.collegefinder.dto.request.CollegeFilterRequest;
import com.collegefinder.entity.College;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CollegeSpecification {

    public static Specification<College> fromFilter(CollegeFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getQ() != null && !filter.getQ().isBlank()) {
                String like = "%" + filter.getQ().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("collegeName")), like),
                        cb.like(cb.lower(root.get("city")), like),
                        cb.like(cb.lower(root.get("state")), like)
                ));
            }
            if (filter.getState() != null && !filter.getState().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("state")), filter.getState().toLowerCase()));
            }
            if (filter.getCity() != null && !filter.getCity().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), filter.getCity().toLowerCase()));
            }
            if (filter.getCourse() != null && !filter.getCourse().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("courses")), "%" + filter.getCourse().toLowerCase() + "%"));
            }
            if (filter.getInstitutionType() != null && !filter.getInstitutionType().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("institutionType")), filter.getInstitutionType().toLowerCase()));
            }
            if (filter.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), filter.getMinRating()));
            }
            if (filter.getMaxFeeLakh() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("annualFeeLakh"), filter.getMaxFeeLakh()));
            }
            if (filter.getMinPlacementPct() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("placementRatePct"), filter.getMinPlacementPct()));
            }
            if (filter.getHostel() != null) {
                predicates.add(cb.equal(root.get("hostel"), filter.getHostel()));
            }
            if (filter.getCutoffExam() != null && !filter.getCutoffExam().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("cutoffExam")), "%" + filter.getCutoffExam().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
