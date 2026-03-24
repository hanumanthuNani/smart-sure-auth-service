package com.smartsure.auth.service;

import com.smartsure.auth.dto.ApiResponse;
import com.smartsure.auth.dto.RegisterRequest;
import com.smartsure.auth.dto.UserResponse;

public interface AuthService {
    ApiResponse<UserResponse> register(RegisterRequest request);
    ApiResponse<UserResponse> login(com.smartsure.auth.dto.LoginRequest request);
}
