package com.example.hotel.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hotel.dto.response.UserResponse;
import com.example.hotel.entity.User;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.UserRepository;
import com.example.hotel.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getUsers() {

        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {

            UserResponse response = new UserResponse();

        
            response.setName(user.getName());
            response.setEmail(user.getEmail());

            return response;

        }).toList();
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado"
                        ));

        UserResponse response = new UserResponse();

        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return response;
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado"
                        ));

        userRepository.delete(user);
    }
}