package com.smartprocure.modules.auth.service.impl;

import com.smartprocure.config.JwtTokenProvider;
import com.smartprocure.config.UserPrincipal;
import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.core.exception.UnauthorizedException;
import com.smartprocure.modules.auth.dto.request.ChangePasswordRequest;
import com.smartprocure.modules.auth.dto.request.LoginRequest;
import com.smartprocure.modules.auth.dto.request.PasswordResetRequest;
import com.smartprocure.modules.auth.dto.request.RegisterRequest;
import com.smartprocure.modules.auth.dto.response.JwtAuthResponse;
import com.smartprocure.modules.auth.dto.response.UserSummaryResponse;
import com.smartprocure.modules.auth.entity.PasswordResetToken;
import com.smartprocure.modules.auth.entity.Role;
import com.smartprocure.modules.auth.entity.User;
import com.smartprocure.modules.auth.enums.UserStatus;
import com.smartprocure.modules.auth.repository.PasswordResetTokenRepository;
import com.smartprocure.modules.auth.repository.RoleRepository;
import com.smartprocure.modules.auth.repository.UserRepository;
import com.smartprocure.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Enterprise Service Implementation for Authentication, Authorization, and Token Lifecycle Management.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public UserSummaryResponse register(RegisterRequest registerRequest) {
        log.info("Attempting user registration for email: {}", registerRequest.getEmail());

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessRuleViolationException("An account with email " + registerRequest.getEmail() + " already exists.");
        }

        Role userRole = roleRepository.findByName(registerRequest.getRole())
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(registerRequest.getRole())
                                .description("Enterprise System Role: " + registerRequest.getRole().name())
                                .build()
                ));

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .phoneNumber(registerRequest.getPhoneNumber())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .roles(Collections.singleton(userRole))
                .build();

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user with ID: {}", savedUser.getId());

        return mapToUserSummary(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public JwtAuthResponse login(LoginRequest loginRequest) {
        log.info("Authenticating user credentials for email: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        UserSummaryResponse userSummary = UserSummaryResponse.builder()
                .id(userPrincipal.getId())
                .firstName(userPrincipal.getFirstName())
                .lastName(userPrincipal.getLastName())
                .email(userPrincipal.getEmail())
                .status(userPrincipal.getStatus())
                .roles(userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .build();

        return JwtAuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .user(userSummary)
                .build();
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        log.info("Initiating password reset request for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        tokenRepository.deleteByUser(user);

        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(tokenString)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        tokenRepository.save(resetToken);
        log.info("Password reset token generated for user ID: {}. Token: {}", user.getId(), tokenString);
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest resetRequest) {
        log.info("Processing password reset with token");
        PasswordResetToken resetToken = tokenRepository.findByToken(resetRequest.getToken())
                .orElseThrow(() -> new BusinessRuleViolationException("Invalid or non-existent reset token"));

        if (resetToken.isUsed()) {
            throw new BusinessRuleViolationException("This password reset token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new BusinessRuleViolationException("This password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password successfully updated for user email: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(String userEmail, ChangePasswordRequest changePasswordRequest) {
        log.info("Processing password change for user: {}", userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedException("Incorrect current password provided");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", userEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return mapToUserSummary(user);
    }

    private UserSummaryResponse mapToUserSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                .build();
    }
}
