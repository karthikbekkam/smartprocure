package com.smartprocure.modules.auth.service;

import com.smartprocure.modules.auth.dto.request.ChangePasswordRequest;
import com.smartprocure.modules.auth.dto.request.LoginRequest;
import com.smartprocure.modules.auth.dto.request.PasswordResetRequest;
import com.smartprocure.modules.auth.dto.request.RegisterRequest;
import com.smartprocure.modules.auth.dto.response.JwtAuthResponse;
import com.smartprocure.modules.auth.dto.response.UserSummaryResponse;

/**
 * Enterprise Service Contract for User Registration, Authentication & Security Workflows.
 *
 * @author Principal Java Architect
 */
public interface AuthService {

    UserSummaryResponse register(RegisterRequest registerRequest);

    JwtAuthResponse login(LoginRequest loginRequest);

    void forgotPassword(String email);

    void resetPassword(PasswordResetRequest resetRequest);

    void changePassword(String userEmail, ChangePasswordRequest changePasswordRequest);

    UserSummaryResponse getCurrentUser(String email);
}
