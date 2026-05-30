package com.example.hotel.controller;

import org.springframework.web.bind.annotation.*;

import com.example.hotel.dto.request.LoginRequest;
import com.example.hotel.dto.request.RegisterRequest;
import com.example.hotel.dto.response.JwtResponse;
import com.example.hotel.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public JwtResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public JwtResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }

}