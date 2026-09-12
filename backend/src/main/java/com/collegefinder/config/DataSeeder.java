package com.collegefinder.config;

import com.collegefinder.entity.College;
import com.collegefinder.repository.CollegeRepository;
import com.collegefinder.util.CityCoordinates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final CollegeRepository collegeRepository;

    @Override
    @Transactional
    public void run(String... args) {

        log.info("==========================================");
        log.info("Starting College Dataset Import");
        log.info("==========================================");

        try {

            /*
             * CSV location:
             *
             * backend/src/main/resources/data/college_dataset.csv
             */
            ClassPathResource resource =
                    new ClassPathResource("data/college_dataset.csv");

            if (!resource.exists()) {
                log.error("college_dataset.csv was not found!");
                return;
            }

            List<College> colleges = new ArrayList<>();

            try (
                    Reader reader = new InputStreamReader(
                            resource.getInputStream(),
                            StandardCharsets.UTF_8
                    );

                    CSVParser parser = CSVFormat.DEFAULT.builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .setTrim(true)
                            .setIgnoreSurroundingSpaces(true)
                            .build()
                            .parse(reader)
            ) {

                for (CSVRecord r : parser) {

                    try {

                        College college = College.builder()

                                .id(parseLong(r.get("id")))

                                .collegeName(
                                        r.get("college_name")
                                )

                                .city(
                                        r.get("city")
                                )

                                .state(
                                        r.get("state")
                                )

                                .institutionType(
                                        r.get("institution_type")
                                )

                                .courses(
                                        r.get("courses")
                                )

                                .annualFeeLakh(
                                        parseDecimal(
                                                r.get("annual_fee_lakh_approx")
                                        )
                                )

                                .cutoffExam(
                                        r.get("cutoff_exam")
                                )

                                .cutoffNote(
                                        r.get("cutoff_note")
                                )

                                .placementRatePct(
                                        parseDecimal(
                                                r.get("placement_rate_pct_approx")
                                        )
                                )

                                .averagePackageLpa(
                                        parseDecimal(
                                                r.get("average_package_lpa_approx")
                                        )
                                )

                                .highestPackageApprox(
                                        r.get("highest_package_approx")
                                )

                                .rating(
                                        parseDecimal(
                                                r.get("rating_demo_out_of_5")
                                        )
                                )

                                .hostel(
                                        parseBoolean(
                                                r.get("hostel")
                                        )
                                )

                                .website(
                                        r.get("website")
                                )

                                .nirfRank(
                                        parseInt(
                                                r.get(
                                                        "nirf_engineering_rank_2025"
                                                )
                                        )
                                )

                                .dataStatus(
                                        r.get("data_status")
                                )

                                .dataReferenceYear(
                                        parseInt(
                                                r.get("data_reference_year")
                                        )
                                )

                                .build();


                        /*
                         * Get latitude and longitude
                         * based on the college city.
                         */
                        double[] coordinates =
                                CityCoordinates.lookup(
                                        college.getCity()
                                );

                        if (coordinates != null) {

                            college.setLatitude(
                                    coordinates[0]
                            );

                            college.setLongitude(
                                    coordinates[1]
                            );
                        }

                        colleges.add(college);

                    } catch (Exception e) {

                        log.error(
                                "Skipping invalid CSV row {}: {}",
                                r.getRecordNumber(),
                                e.getMessage()
                        );
                    }
                }
            }

            /*
             * Make sure the CSV actually contains data.
             */
            if (colleges.isEmpty()) {

                log.error(
                        "No valid college records found in CSV. "
                                + "Database was NOT changed."
                );

                return;
            }

            log.info(
                    "Loaded {} colleges from CSV.",
                    colleges.size()
            );

            /*
             * IMPORTANT:
             *
             * DO NOT use:
             *
             * collegeRepository.deleteAll();
             *
             * because recommendations reference colleges
             * through foreign keys.
             *
             * saveAll() will:
             *
             * - update existing IDs
             * - insert new IDs
             */
            collegeRepository.saveAll(colleges);

            /*
             * Flush changes to the database.
             */
            collegeRepository.flush();

            long total =
                    collegeRepository.count();

            log.info(
                    "Successfully imported/updated {} colleges.",
                    colleges.size()
            );

            log.info(
                    "Total colleges currently in database: {}",
                    total
            );

            log.info("==========================================");
            log.info("College Dataset Import Completed");
            log.info("==========================================");

        } catch (Exception e) {

            log.error(
                    "College dataset import failed!",
                    e
            );

            throw new RuntimeException(
                    "College dataset import failed",
                    e
            );
        }
    }


    private BigDecimal parseDecimal(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return new BigDecimal(
                    value.trim()
            );

        } catch (NumberFormatException e) {

            log.warn(
                    "Invalid decimal value: {}",
                    value
            );

            return null;
        }
    }


    private Integer parseInt(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return Integer.parseInt(
                    value.trim()
            );

        } catch (NumberFormatException e) {

            log.warn(
                    "Invalid integer value: {}",
                    value
            );

            return null;
        }
    }


    private Long parseLong(String value) {

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    "College ID cannot be empty"
            );
        }

        try {

            return Long.parseLong(
                    value.trim()
            );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Invalid college ID: " + value
            );
        }
    }


    private boolean parseBoolean(String value) {

        if (value == null || value.isBlank()) {
            return false;
        }

        return value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("true")
                || value.equals("1");
    }
}