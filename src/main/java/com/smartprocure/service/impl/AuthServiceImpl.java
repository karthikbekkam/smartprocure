package com.smartprocure.service.impl;

import com.smartprocure.domain.entity.Role;
import com.smartprocure.domain.entity.User;
import com.smartprocure.domain.enums.RoleType;
import com.smartprocure.domain.repository.RoleRepository;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.dto.request.LoginRequest;
import com.smartprocure.dto.request.RegisterRequest;
import com.smartprocure.dto.response.JwtAuthResponse;
import com.smartprocure.dto.response.UserResponseDTO;
import com.smartprocure.exception.BadRequestException;
import com.smartprocure.exception.DuplicateResourceException;
import com.smartprocure.exception.ResourceNotFoundException;
import com.smartprocure.security.JwtTokenProvider;
import com.smartprocure.service.AuthService;
import com.smartprocure.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;


    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    public JwtAuthResponse login(LoginRequest loginRequest) {

        String email = loginRequest.getEmail()
                .trim()
                .toLowerCase();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                loginRequest.getPassword()
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "email",
                                email
                        )
                );

        UserResponseDTO userDTO =
                mapToUserResponse(user);

        return JwtAuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(userDTO)
                .build();
    }


    // =========================================================
    // REGISTRATION
    // =========================================================

    @Override
    @Transactional
    public UserResponseDTO register(
            RegisterRequest registerRequest) {

        // -----------------------------------------------------
        // 1. Validate email
        // -----------------------------------------------------

        if (registerRequest.getEmail() == null
                || registerRequest.getEmail().isBlank()) {

            throw new BadRequestException(
                    "Email address is required."
            );
        }

        String email = registerRequest.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmail(email)) {

            throw new DuplicateResourceException(
                    "User already exists with email: " + email
            );
        }


        // -----------------------------------------------------
        // 2. Validate role
        // -----------------------------------------------------

        if (registerRequest.getRoleName() == null
                || registerRequest.getRoleName().isBlank()) {

            throw new BadRequestException(
                    "Role is required."
            );
        }

        String requestedRole = registerRequest.getRoleName()
                .trim()
                .toUpperCase()
                .replace("-", "_")
                .replace(" ", "_");


        // -----------------------------------------------------
        // 3. ADMIN cannot self-register
        // -----------------------------------------------------

        if (requestedRole.equals("ADMIN")
                || requestedRole.equals("ADMINISTRATOR")
                || requestedRole.equals("ROLE_ADMIN")
                || requestedRole.equals("ROLE_ADMINISTRATOR")) {

            throw new BadRequestException(
                    "Public self-registration as Administrator is restricted."
            );
        }


        // -----------------------------------------------------
        // 4. Convert frontend role to EXACT RoleType enum
        // -----------------------------------------------------

        final RoleType roleType;

        switch (requestedRole) {

            // PROCUREMENT
            case "PROCUREMENT":
            case "PROCUREMENT_MANAGER":
            case "ROLE_PROCUREMENT":
            case "ROLE_PROCUREMENT_MANAGER":

                roleType = RoleType.ROLE_PROCUREMENT_MANAGER;
                break;


            // FINANCE
            case "FINANCE":
            case "FINANCE_MANAGER":
            case "ROLE_FINANCE":
            case "ROLE_FINANCE_MANAGER":

                roleType = RoleType.ROLE_FINANCE_MANAGER;
                break;


            // WAREHOUSE
            case "WAREHOUSE":
            case "WAREHOUSE_MANAGER":
            case "ROLE_WAREHOUSE":
            case "ROLE_WAREHOUSE_MANAGER":

                roleType = RoleType.ROLE_WAREHOUSE_MANAGER;
                break;


            // VENDOR
            case "VENDOR":
            case "SUPPLIER":
            case "ROLE_VENDOR":
            case "ROLE_SUPPLIER":

                roleType = RoleType.ROLE_VENDOR;
                break;


            // INVALID
            default:

                throw new BadRequestException(
                        "Invalid role specified: "
                                + registerRequest.getRoleName()
                                + ". Supported roles are PROCUREMENT, FINANCE, WAREHOUSE and VENDOR."
                );
        }


        // -----------------------------------------------------
        // 5. Find role in database
        // -----------------------------------------------------

        Role role = roleRepository
                .findByName(roleType)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role",
                                "name",
                                roleType.name()
                        )
                );


        // -----------------------------------------------------
        // 6. Create role set
        // -----------------------------------------------------

        Set<Role> roles = new HashSet<>();
        roles.add(role);


        // -----------------------------------------------------
        // 7. Create user
        // -----------------------------------------------------

        User user = User.builder()
                .email(email)
                .password(
                        passwordEncoder.encode(
                                registerRequest.getPassword()
                        )
                )
                .firstName(
                        registerRequest.getFirstName()
                                .trim()
                )
                .lastName(
                        registerRequest.getLastName()
                                .trim()
                )
                .phone(
                        registerRequest.getPhone() != null
                                ? registerRequest.getPhone().trim()
                                : null
                )
                .active(true)
                .verified(true)
                .roles(roles)
                .build();


        // -----------------------------------------------------
        // 8. Save user
        // -----------------------------------------------------

        User savedUser =
                userRepository.save(user);

        return mapToUserResponse(savedUser);
    }


    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    @Override
    @Transactional
    public void forgotPassword(String email) {

        if (email == null || email.isBlank()) {

            throw new BadRequestException(
                    "Email address is required."
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        userRepository
                .findByEmail(normalizedEmail)
                .ifPresent(user -> {

                    String token =
                            UUID.randomUUID().toString();

                    user.setResetPasswordToken(token);

                    userRepository.save(user);

                    emailService.sendPasswordResetEmail(
                            user.getEmail(),
                            token
                    );
                });
    }


    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @Override
    @Transactional
    public void resetPassword(
            String token,
            String newPassword) {

        if (token == null || token.isBlank()) {

            throw new BadRequestException(
                    "Invalid password reset request."
            );
        }

        if (newPassword == null
                || newPassword.length() < 8) {

            throw new BadRequestException(
                    "Password must contain at least 8 characters."
            );
        }

        User user =
                userRepository
                        .findByResetPasswordToken(token)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid or expired password reset token."
                                )
                        );

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        // Token can only be used once
        user.setResetPasswordToken(null);

        userRepository.save(user);
    }


    // =========================================================
    // USER RESPONSE MAPPING
    // =========================================================

    private UserResponseDTO mapToUserResponse(
            User user) {

        Set<String> roleNames =
                user.getRoles()
                        .stream()
                        .map(role ->
                                role.getName().name()
                        )
                        .collect(Collectors.toSet());

        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .active(user.getActive())
                .verified(user.getVerified())
                .roles(roleNames)
                .build();
    }
}