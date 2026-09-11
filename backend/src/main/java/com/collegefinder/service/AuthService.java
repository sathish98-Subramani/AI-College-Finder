package com.collegefinder.service;

import com.collegefinder.dto.request.LoginRequest;
import com.collegefinder.dto.request.RegisterRequest;
import com.collegefinder.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
