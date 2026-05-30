package com.example.hotel.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotel.entity.Reservation;

@Repository
public interface ReservationRepository
                extends JpaRepository<Reservation, Long> {
        boolean existsByRoomIdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        Long roomId,
                        LocalDate checkOut,
                        LocalDate checkIn);
}
