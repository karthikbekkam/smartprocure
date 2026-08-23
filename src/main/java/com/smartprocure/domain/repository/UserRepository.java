package com.smartprocure.domain.repository;

import com.smartprocure.domain.entity.User;
import com.smartprocure.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRolesName(RoleType roleName);
    Optional<User> findByResetPasswordToken(String token);
    Optional<User> findByVerificationToken(String token);
}
