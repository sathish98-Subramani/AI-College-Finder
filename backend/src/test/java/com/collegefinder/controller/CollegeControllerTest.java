package com.collegefinder.controller;

import com.collegefinder.entity.College;
import com.collegefinder.repository.CollegeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CollegeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollegeRepository collegeRepository;

    @BeforeEach
    void seed() {
        collegeRepository.deleteAll();
        collegeRepository.save(College.builder()
                .id(1L).collegeName("Test Institute of Technology")
                .city("Chennai").state("Tamil Nadu").institutionType("IIT")
                .courses("CSE; ECE").annualFeeLakh(new BigDecimal("2.4"))
                .cutoffExam("JEE Advanced").placementRatePct(new BigDecimal("90"))
                .averagePackageLpa(new BigDecimal("15")).rating(new BigDecimal("4.5"))
                .hostel(true).nirfRank(5).build());
    }

    @Test
    void listCollegesReturnsSeededCollege() throws Exception {
        mockMvc.perform(get("/api/colleges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].collegeName").value("Test Institute of Technology"));
    }

    @Test
    void filterByStateReturnsMatch() throws Exception {
        mockMvc.perform(get("/api/colleges").param("state", "Tamil Nadu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void filterByWrongStateReturnsEmpty() throws Exception {
        mockMvc.perform(get("/api/colleges").param("state", "Kerala"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getByIdReturnsCollege() throws Exception {
        mockMvc.perform(get("/api/colleges/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collegeName").value("Test Institute of Technology"));
    }
}
