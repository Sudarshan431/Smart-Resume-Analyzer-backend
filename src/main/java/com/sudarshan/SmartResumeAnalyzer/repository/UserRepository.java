package com.sudarshan.SmartResumeAnalyzer.repository;

import com.sudarshan.SmartResumeAnalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (used in login & auth)
    Optional<User> findByEmail(String email);

    // Check if email already exists (used in register)
    boolean existsByEmail(String email);
}