package com.collegefinder.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // --------------------------------------------------
        // IMPORTANT:
        // CORS preflight requests use OPTIONS.
        // They should NOT require JWT authentication.
        // --------------------------------------------------
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get Authorization header
        String header = request.getHeader("Authorization");

        // No JWT token -> continue normally
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token
        String token = header.substring(7);

        try {
            // Extract email from JWT
            String email = jwtUtil.extractEmail(token);

            if (email != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                // Validate token
                if (jwtUtil.isTokenValid(
                        token,
                        userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (Exception ignored) {
            // Invalid/expired JWT.
            // Continue as unauthenticated.
            // Spring Security will decide whether the endpoint
            // requires authentication.
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}