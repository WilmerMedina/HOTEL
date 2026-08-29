package com.example.hotel.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.hotel.dto.request.UserRequest;
import com.example.hotel.dto.response.UserResponse;


public interface UserService {
    Page<UserResponse> getUsers(Pageable pageable);

    UserResponse getUserById(Long id);

    void deleteUser(Long id);

    UserResponse updateUser(Long id, UserRequest request);

}
