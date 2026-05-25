package com.symmetricsquad.hms_backend.controller;

import com.symmetricsquad.hms_backend.dto.request.LoginRequest;
import com.symmetricsquad.hms_backend.dto.request.UserRequest;
import com.symmetricsquad.hms_backend.dto.response.AuthResponse;
import com.symmetricsquad.hms_backend.dto.response.UserResponse;
import com.symmetricsquad.hms_backend.security.JwtTokenProvider;
import com.symmetricsquad.hms_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // POST /api/auth/users/register
    // Kept from original AuthController + UserController — single registration endpoint
    @PostMapping("/users/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    // POST /api/auth/users/login
    // Kept from original — patients log in here
    @PostMapping("/users/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request) {
        UserResponse user = userService.loginUser(request.getEmail(), request.getPassword());
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/doctors/login
    // Kept from original — doctors use same User table, same flow
    @PostMapping("/doctors/login")
    public ResponseEntity<AuthResponse> loginDoctor(@RequestBody LoginRequest request) {
        UserResponse user = userService.loginUser(request.getEmail(), request.getPassword());
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/admin/login
    // Kept from original AdminController — same service, role check done by JWT filter
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> loginAdmin(@RequestBody LoginRequest request) {
        UserResponse user = userService.loginUser(request.getEmail(), request.getPassword());
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        return ResponseEntity.ok(response);
    }
}
