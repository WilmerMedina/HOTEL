package com.example.hotel.service;

import java.util.List;

import com.example.hotel.dto.request.UserRequest;
import com.example.hotel.dto.response.UserResponse;


public interface UserService {
    List<UserResponse> getUsers();

    UserResponse getUserById(Long id);

    void deleteUser(Long id);

    UserResponse updateUser(Long id, UserRequest request);

}
