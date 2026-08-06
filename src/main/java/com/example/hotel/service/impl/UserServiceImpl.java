package com.example.hotel.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hotel.dto.request.UserRequest;
import com.example.hotel.dto.response.UserResponse;
import com.example.hotel.entity.User;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.mapper.UserMapper;
import com.example.hotel.repository.UserRepository;
import com.example.hotel.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponse> getUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        userRepository.delete(user);
    }


    @Override
    public UserResponse updateUser(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        userMapper.updateEntity(user, request);

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

}