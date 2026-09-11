package com.collegefinder.service;

import com.collegefinder.dto.request.CollegeFilterRequest;
import com.collegefinder.dto.request.CollegeRequest;
import com.collegefinder.dto.response.CollegeResponse;
import com.collegefinder.dto.response.ImportSummaryResponse;
import com.collegefinder.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CollegeService {
    PageResponse<CollegeResponse> search(CollegeFilterRequest filter);
    CollegeResponse getById(Long id);
    List<CollegeResponse> similarColleges(Long id, int limit);
    CollegeResponse create(CollegeRequest request);
    CollegeResponse update(Long id, CollegeRequest request);
    void delete(Long id);
    ImportSummaryResponse importCsv(MultipartFile file);
    List<CollegeResponse> compare(List<Long> ids);
}
