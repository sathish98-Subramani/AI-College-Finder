package com.collegefinder.repository;

import com.collegefinder.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long>, JpaSpecificationExecutor<College> {
    Optional<College> findByCollegeNameIgnoreCase(String collegeName);
    List<College> findByStateIgnoreCase(String state);
    List<College> findByCityIgnoreCase(String city);
}
