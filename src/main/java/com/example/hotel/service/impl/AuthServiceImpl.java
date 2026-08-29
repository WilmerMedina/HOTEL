package com.example.hotel.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

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

        String email = request.getEmail().trim().toLowerCase();

        boolean emailExists = userRepository.findByEmail(email).isPresent();

        if (emailExists) {
            log.warn("Intento de registro con email ya existente: {}", email);
            throw new ConflictException("El correo ya está registrado");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CLIENT); 

        User savedUser = userRepository.save(user);

        log.info("Nuevo usuario registrado: {}", email);

        String token = jwtService.generateToken(savedUser.getEmail());

        JwtResponse response = new JwtResponse();
        response.setToken(token);
        return response;
    }

    @Override
    public JwtResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Intento de login con email inexistente: {}", email);
                    return new AuthenticationException("Correo o contraseña incorrectos");
                });

        // Se valida el estado de la cuenta ANTES de revisar la contraseña.
        // Sin esto, una cuenta bloqueada/desactivada podía seguir
        // autenticándose y obteniendo tokens nuevos, sin importar lo que
        // hiciéramos en CustomUserDetailsService — ese componente solo
        // protege peticiones posteriores con un token ya emitido, no el
        // login en sí, porque este flujo no pasa por AuthenticationManager.
        if (!user.isActive()) {
            log.warn("Intento de login en cuenta desactivada: {}", email);
            throw new AuthenticationException("Correo o contraseña incorrectos");
        }

        if (user.isLocked()) {
            log.warn("Intento de login en cuenta bloqueada: {}", email);
            throw new AuthenticationException("Correo o contraseña incorrectos");
        }

        boolean passwordMatches =
                passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            log.warn("Intento de login fallido (contraseña incorrecta): {}", email);
            throw new AuthenticationException("Correo o contraseña incorrectos");
        }

        log.info("Login exitoso: {}", email);

        String token = jwtService.generateToken(user.getEmail());

        JwtResponse response = new JwtResponse();
        response.setToken(token);
        return response;
    }
}