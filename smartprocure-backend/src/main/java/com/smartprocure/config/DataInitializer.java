package com.smartprocure.config;

import com.smartprocure.modules.auth.entity.Role;
import com.smartprocure.modules.auth.entity.User;
import com.smartprocure.modules.auth.enums.ERole;
import com.smartprocure.modules.auth.enums.UserStatus;
import com.smartprocure.modules.auth.repository.RoleRepository;
import com.smartprocure.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Enterprise Database Data Initializer seeding essential system roles and the super-admin account on startup.
 *
 * @author Principal Java Architect
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Checking system roles and initial seed data...");

        for (ERole eRole : ERole.values()) {
            roleRepository.findByName(eRole).orElseGet(() -> {
                Role newRole = Role.builder()
                        .name(eRole)
                        .description("System Enterprise Role: " + eRole.name())
                        .build();
                return roleRepository.save(newRole);
            });
        }

        String adminEmail = "admin@smartprocure.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

            User adminUser = User.builder()
                    .firstName("System")
                    .lastName("Administrator")
                    .email(adminEmail)
                    .phoneNumber("+1234567890")
                    .password(passwordEncoder.encode("Admin@123456"))
                    .status(UserStatus.ACTIVE)
                    .emailVerified(true)
                    .roles(Collections.singleton(adminRole))
                    .build();

            userRepository.save(adminUser);
            log.info("Default Super Admin user successfully seeded: {}", adminEmail);
        }
    }
}
