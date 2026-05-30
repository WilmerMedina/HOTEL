package com.example.hotel.service;

import com.example.hotel.dto.request.LoginRequest;
import com.example.hotel.dto.response.JwtResponse;
import com.example.hotel.dto.request.RegisterRequest;

public interface AuthService {

    JwtResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request);
}