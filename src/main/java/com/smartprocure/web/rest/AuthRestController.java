package com.smartprocure.web.rest;

import com.smartprocure.dto.request.ForgotPasswordRequest;
import com.smartprocure.dto.request.LoginRequest;
import com.smartprocure.dto.request.RegisterRequest;
import com.smartprocure.dto.request.ResetPasswordRequest;
import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.dto.response.JwtAuthResponse;
import com.smartprocure.dto.response.UserResponseDTO;
import com.smartprocure.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication Management",
        description = "Endpoints for Login, Registration, JWT Token verification, and Password Reset"
)
public class AuthRestController {

    private static final String AUTH_COOKIE = "SMARTPROCURE_AUTH";

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Authenticate user credentials and return JWT access token"
    )
    public ResponseEntity<ApiResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {

        JwtAuthResponse response = authService.login(loginRequest);

        ResponseCookie authCookie = ResponseCookie
                .from(AUTH_COOKIE, response.getAccessToken())
                .httpOnly(true)
                .secure(false) // Change to true when deployed with HTTPS
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .body(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register")
    @Operation(
            summary = "User Registration",
            description = "Register a new enterprise user account"
    )
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {

        UserResponseDTO response =
                authService.register(registerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.ok(
                                "User registered successfully",
                                response
                        )
                );
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Forgot Password",
            description = "Generate password reset token for account email"
    )
    public ResponseEntity<ApiResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "If an account exists for this email, password reset instructions have been sent."
                )
        );
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset Password",
            description = "Set a new password using a valid reset token"
    )
    public ResponseEntity<ApiResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Password has been reset successfully"
                )
        );
    }

    @PostMapping("/logout")
    @Operation(
            summary = "User Logout",
            description = "Clear the SmartProcure authentication cookie"
    )
    public ResponseEntity<ApiResponse> logout() {

        ResponseCookie deleteCookie = ResponseCookie
                .from(AUTH_COOKIE, "")
                .httpOnly(true)
                .secure(false) // Change to true when deployed with HTTPS
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.ok("Logout successful"));
    }
}