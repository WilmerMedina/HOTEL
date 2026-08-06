package com.example.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.hotel.entity.Room;


public interface RoomRepository
        extends JpaRepository<Room, Long> {
}