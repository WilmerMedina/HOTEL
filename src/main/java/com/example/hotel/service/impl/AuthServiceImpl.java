
package com.example.hotel.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.hotel.dto.request.LoginRequest;
import com.example.hotel.dto.request.RegisterRequest;
import com.example.hotel.dto.response.JwtResponse;
import com.example.hotel.entity.User;
import com.example.hotel.enums.Role;
import com.example.hotel.exception.AuthenticationException;
import com.example.hotel.exception.ConflictException;
import com.example.hotel.repository.UserRepository;
import com.example.hotel.security.JwtService;
import com.example.hotel.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public JwtResponse register(RegisterRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        boolean emailExists = userRepository
                .findByEmail(email)
                .isPresent();

        if (emailExists) {
            throw new ConflictException(
                    "El correo ya está registrado");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        user.setRole(Role.CLIENT);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getEmail());

        JwtResponse response = new JwtResponse();

        response.setToken(token);

        return response;
    }

    @Override
    public JwtResponse login(LoginRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new AuthenticationException(
                                "Correo o contraseña incorrectos"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword());

        if (!passwordMatches) {
            throw new AuthenticationException(
                    "Correo o contraseña incorrectos");
        }

        String token = jwtService.generateToken(
                user.getEmail());

        JwtResponse response = new JwtResponse();

        response.setToken(token);

        return response;
    }
}

