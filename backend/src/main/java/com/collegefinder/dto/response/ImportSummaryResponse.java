package com.collegefinder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportSummaryResponse {
    private int totalRecords;
    private int inserted;
    private int updated;
    private int duplicates;
    private int failed;
    private List<String> errors;
}
