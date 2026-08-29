package com.example.hotel.repository;

import java.time.LocalDate;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotel.entity.Reservation;

public interface ReservationRepository
                extends JpaRepository<Reservation, Long> {

        boolean existsByRoomIdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        Long roomId,
                        LocalDate checkOut,
                        LocalDate checkIn);


       Page<Reservation> findByUserId(Long userId, Pageable pageable);
}