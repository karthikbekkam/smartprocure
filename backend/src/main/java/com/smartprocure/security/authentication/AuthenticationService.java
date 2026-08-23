package com.smartprocure.security.authentication;

import com.smartprocure.common.exception.BadRequestException;
import com.smartprocure.common.exception.DuplicateResourceException;
import com.smartprocure.domain.Role;
import com.smartprocure.domain.User;
import com.smartprocure.domain.repository.RoleRepository;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.dto.auth.JwtAuthenticationResponse;
import com.smartprocure.dto.auth.LoginRequest;
import com.smartprocure.dto.auth.RegisterRequest;
import com.smartprocure.security.jwt.JwtTokenProvider;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return JwtAuthenticationResponse.builder()
                .accessToken(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .build();
    }

    @Transactional
    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + registerRequest.getEmail());
        }

        Set<Role> roles = new HashSet<>();
        String roleName = registerRequest.getRole() != null ? registerRequest.getRole() : "VENDOR";
        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Default role not found: " + roleName));
        roles.add(userRole);

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phone(registerRequest.getPhone())
                .active(true)
                .verified(true)
                .roles(roles)
                .build();

        userRepository.save(user);

        return "User registered successfully!";
    }
}
