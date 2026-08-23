package com.smartprocure.security.authentication;

import com.smartprocure.common.exception.DuplicateResourceException;
import com.smartprocure.domain.Role;
import com.smartprocure.domain.User;
import com.smartprocure.domain.repository.RoleRepository;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.dto.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("John", "Doe", "john@example.com", "Password@123", "+1234567890", "VENDOR");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(roleRepository.findByName("VENDOR")).thenReturn(Optional.of(Role.builder().name("VENDOR").build()));
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword");

        String result = authenticationService.register(registerRequest);

        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authenticationService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}
