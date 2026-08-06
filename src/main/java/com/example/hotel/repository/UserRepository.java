package com.example.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.hotel.entity.User;


public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}