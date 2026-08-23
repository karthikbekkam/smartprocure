package com.smartprocure.modules.auth.controller;

import com.smartprocure.core.payload.ApiResponse;
import com.smartprocure.modules.auth.dto.request.ChangePasswordRequest;
import com.smartprocure.modules.auth.dto.request.LoginRequest;
import com.smartprocure.modules.auth.dto.request.PasswordResetRequest;
import com.smartprocure.modules.auth.dto.request.RegisterRequest;
import com.smartprocure.modules.auth.dto.response.JwtAuthResponse;
import com.smartprocure.modules.auth.dto.response.UserSummaryResponse;
import com.smartprocure.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Enterprise REST Controller exposing authentication & user credential management endpoints.
 *
 * @author Principal Java Architect
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & Access Management", description = "Endpoints for Sign-In, User Registration, JWT Issuance, and Password Recovery")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register New System User", description = "Creates a new user account with specified enterprise role.")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserSummaryResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(ApiResponse.success(response, "User registered successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate User Sign-In", description = "Authenticates user credentials and returns JWT bearer access token.")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Authentication successful"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate Forgot Password Workflow", description = "Generates a password reset token for the specified user email.")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam("email") String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset token has been dispatched successfully"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Account Password", description = "Resets user password using valid token.")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        authService.resetPassword(resetRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Password has been reset successfully"));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change Password (Authenticated User)", description = "Updates password for currently authenticated user.")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(authentication.getName(), changePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Password updated successfully"));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Currently Logged-in User Profile", description = "Returns sanitized profile summary of authenticated user.")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getCurrentUserProfile(Authentication authentication) {
        UserSummaryResponse userSummary = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(userSummary, "User profile retrieved successfully"));
    }
}
