package com.smartprocure.service;

import com.smartprocure.dto.request.LoginRequest;
import com.smartprocure.dto.request.RegisterRequest;
import com.smartprocure.dto.response.JwtAuthResponse;
import com.smartprocure.dto.response.UserResponseDTO;

public interface AuthService {
    JwtAuthResponse login(LoginRequest loginRequest);
    UserResponseDTO register(RegisterRequest registerRequest);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
