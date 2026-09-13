package com.example.hotel.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotel.dto.request.ChangePasswordRequest;
import com.example.hotel.dto.request.UserRequest;
import com.example.hotel.dto.response.UserResponse;
import com.example.hotel.entity.User;
import com.example.hotel.exception.InvalidPasswordException;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.mapper.UserMapper;
import com.example.hotel.repository.UserRepository;
import com.example.hotel.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        log.info("Solicitud de cambio de contraseña para usuario: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", email);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            log.warn("Contraseña actual incorrecta para usuario: {}", email);

            throw new InvalidPasswordException(
                    "La contraseña actual es incorrecta");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        log.info("Contraseña actualizada correctamente para usuario: {}", email);
    }

}