package com.example.hotel.mapper;

import org.springframework.stereotype.Component;

import com.example.hotel.dto.request.UserRequest;
import com.example.hotel.dto.response.UserResponse;
import com.example.hotel.entity.User;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();

        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        response.setLocked(user.isLocked());

        return response;
    }

    public User toEntity(UserRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        return user;
    }

    public void updateEntity(User user, UserRequest request) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
    }

}