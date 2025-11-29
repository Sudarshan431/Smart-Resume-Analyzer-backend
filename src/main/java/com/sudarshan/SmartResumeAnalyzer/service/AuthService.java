package com.sudarshan.SmartResumeAnalyzer.service;

import com.sudarshan.SmartResumeAnalyzer.dto.auth.AuthResponse;
import com.sudarshan.SmartResumeAnalyzer.dto.auth.LoginRequest;
import com.sudarshan.SmartResumeAnalyzer.dto.auth.RegisterRequest;
import com.sudarshan.SmartResumeAnalyzer.entity.User;
import com.sudarshan.SmartResumeAnalyzer.repository.UserRepository;
import com.sudarshan.SmartResumeAnalyzer.security.JwtUtil;
import com.sudarshan.SmartResumeAnalyzer.exception.BadRequestException;
import com.sudarshan.SmartResumeAnalyzer.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 1️⃣ Register new user
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);
    }

    // 2️⃣ Login & return JWT
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getName(), user.getEmail());
    }

    // 3️⃣ Get current user ID from SecurityContext (after JWT filter runs)
    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String email = userDetails.getUsername();
            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
        }
        throw new UnauthorizedException("Invalid authentication");
    }
}
