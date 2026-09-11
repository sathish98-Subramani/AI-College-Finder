package com.collegefinder.config;

import com.collegefinder.security.CustomUserDetailsService;
import com.collegefinder.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // ---------------- PASSWORD ENCODER ----------------

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ---------------- AUTHENTICATION MANAGER ----------------

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    // ---------------- AUTHENTICATION PROVIDER ----------------

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // ---------------- SECURITY FILTER CHAIN ----------------

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF because we are using JWT
                .csrf(csrf -> csrf.disable())

                // Enable CORS
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                // Stateless JWT authentication
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // IMPORTANT:
                        // Allow browser CORS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Authentication APIs
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public college APIs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/colleges/**"
                        ).permitAll()

                        // Health and actuator
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/health",
                                "/api/actuator/**"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Compare colleges
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/compare"
                        ).permitAll()

                        // AI chat
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/ai/chat"
                        ).permitAll()

                        // Admin college operations
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/colleges/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/colleges/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/colleges/**"
                        ).hasRole("ADMIN")

                        // Admin APIs
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Authentication provider
                .authenticationProvider(authenticationProvider())

                // JWT filter
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ---------------- CORS CONFIGURATION ----------------

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // Vercel frontend + local development
        configuration.setAllowedOrigins(
                List.of(
                        frontendUrl,
                        "http://localhost:5173"
                )
        );

        // Allowed HTTP methods
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Allow all request headers
        configuration.setAllowedHeaders(
                List.of("*")
        );

        // Allow Authorization cookies/credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}