package com.sudarshan.SmartResumeAnalyzer.controller;

import com.sudarshan.SmartResumeAnalyzer.dto.auth.AuthResponse;
import com.sudarshan.SmartResumeAnalyzer.dto.auth.LoginRequest;
import com.sudarshan.SmartResumeAnalyzer.dto.auth.RegisterRequest;
import com.sudarshan.SmartResumeAnalyzer.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
