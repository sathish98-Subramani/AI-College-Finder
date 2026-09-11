package com.collegefinder.config;

import com.collegefinder.entity.Role;
import com.collegefinder.entity.User;
import com.collegefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the initial admin account from ADMIN_EMAIL / ADMIN_PASSWORD
 * environment variables, if that account doesn't already exist. Leave
 * ADMIN_EMAIL unset to skip. Safe to run on every startup.
 */
@Component
@RequiredArgsConstructor
@Order(2)
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            log.info("ADMIN_EMAIL/ADMIN_PASSWORD not set - skipping admin account seeding");
            return;
        }
        if (userRepository.existsByEmail(adminEmail.toLowerCase())) {
            log.info("Admin account already exists for {}", adminEmail);
            return;
        }
        User admin = User.builder()
                .fullName("Administrator")
                .email(adminEmail.toLowerCase())
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
        log.info("Created admin account for {}", adminEmail);
    }
}
