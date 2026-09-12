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

        log.info("==============================================");
        log.info("Starting College Dataset Seeder...");
        log.info("==============================================");

        try {

            ClassPathResource resource =
                    new ClassPathResource("data/college_dataset.csv");

            if (!resource.exists()) {
                log.error("college_dataset.csv not found!");
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

                log.info("CSV file loaded successfully.");

                for (CSVRecord r : parser) {

                    try {

                        College college = College.builder()

                                .id(parseLong(r.get("id")))

                                .collegeName(
                                        getValue(r, "college_name")
                                )

                                .city(
                                        getValue(r, "city")
                                )

                                .state(
                                        getValue(r, "state")
                                )

                                .institutionType(
                                        getValue(r, "institution_type")
                                )

                                .courses(
                                        getValue(r, "courses")
                                )

                                .annualFeeLakh(
                                        parseDecimal(
                                                getValue(
                                                        r,
                                                        "annual_fee_lakh_approx"
                                                )
                                        )
                                )

                                .cutoffExam(
                                        getValue(r, "cutoff_exam")
                                )

                                .cutoffNote(
                                        getValue(r, "cutoff_note")
                                )

                                .placementRatePct(
                                        parseDecimal(
                                                getValue(
                                                        r,
                                                        "placement_rate_pct_approx"
                                                )
                                        )
                                )

                                .averagePackageLpa(
                                        parseDecimal(
                                                getValue(
                                                        r,
                                                        "average_package_lpa_approx"
                                                )
                                        )
                                )

                                .highestPackageApprox(
                                        getValue(
                                                r,
                                                "highest_package_approx"
                                        )
                                )

                                .rating(
                                        parseDecimal(
                                                getValue(
                                                        r,
                                                        "rating_demo_out_of_5"
                                                )
                                        )
                                )

                                .hostel(
                                        parseBoolean(
                                                getValue(r, "hostel")
                                        )
                                )

                                .website(
                                        getValue(r, "website")
                                )

                                .nirfRank(
                                        parseInt(
                                                getValue(
                                                        r,
                                                        "nirf_engineering_rank_2025"
                                                )
                                        )
                                )

                                .dataStatus(
                                        getValue(r, "data_status")
                                )

                                .dataReferenceYear(
                                        parseInt(
                                                getValue(
                                                        r,
                                                        "data_reference_year"
                                                )
                                        )
                                )

                                .build();

                        /*
                         * Add latitude and longitude
                         * based on city name.
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

                        } else {

                            log.warn(
                                    "Coordinates not found for city: {}",
                                    college.getCity()
                            );
                        }

                        colleges.add(college);

                    } catch (Exception e) {

                        /*
                         * If one CSV row is invalid,
                         * don't immediately stop processing.
                         */
                        log.error(
                                "Error processing CSV row {}: {}",
                                r.getRecordNumber(),
                                e.getMessage()
                        );
                    }
                }
            }

            log.info(
                    "Total valid colleges read from CSV: {}",
                    colleges.size()
            );

            if (colleges.isEmpty()) {

                log.error(
                        "No valid college records found. "
                                + "Database will NOT be modified."
                );

                return;
            }

            /*
             * IMPORTANT:
             *
             * Remove existing college records only when
             * valid data has successfully been loaded.
             */
            long existingCount =
                    collegeRepository.count();

            log.info(
                    "Existing college records: {}",
                    existingCount
            );

            /*
             * Delete old dataset.
             *
             * This is safe only if your database does not have
             * foreign-key records referencing colleges.
             */
            if (existingCount > 0) {

                log.info(
                        "Removing old college dataset..."
                );

                collegeRepository.deleteAll();

                log.info(
                        "Old college dataset removed."
                );
            }

            /*
             * Insert new dataset.
             */
            collegeRepository.saveAll(colleges);

            log.info(
                    "Successfully inserted {} colleges.",
                    colleges.size()
            );

            log.info("==============================================");
            log.info("College Dataset Seeder Completed Successfully");
            log.info("==============================================");

        } catch (Exception e) {

            log.error(
                    "College Dataset Seeder Failed!",
                    e
            );

            /*
             * Don't hide the real error.
             * Render logs will show the actual exception.
             */
            throw new RuntimeException(
                    "College dataset seeding failed",
                    e
            );
        }
    }

    /**
     * Safely get CSV value.
     */
    private String getValue(
            CSVRecord record,
            String column
    ) {

        try {

            String value = record.get(column);

            if (value == null) {
                return null;
            }

            value = value.trim();

            return value.isEmpty() ? null : value;

        } catch (Exception e) {

            log.warn(
                    "Column '{}' missing in CSV row {}",
                    column,
                    record.getRecordNumber()
            );

            return null;
        }
    }

    /**
     * Parse BigDecimal safely.
     */
    private BigDecimal parseDecimal(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return new BigDecimal(
                    value.trim()
            );

        } catch (NumberFormatException e) {

            log.warn(
                    "Invalid decimal value: '{}'",
                    value
            );

            return null;
        }
    }

    /**
     * Parse Integer safely.
     */
    private Integer parseInt(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return Integer.parseInt(
                    value.trim()
            );

        } catch (NumberFormatException e) {

            log.warn(
                    "Invalid integer value: '{}'",
                    value
            );

            return null;
        }
    }

    /**
     * Parse Long safely.
     */
    private Long parseLong(
            String value
    ) {

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

    /**
     * Parse Yes/No values.
     */
    private boolean parseBoolean(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return false;
        }

        return value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("1");
    }
}
