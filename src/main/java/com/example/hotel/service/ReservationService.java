package com.example.hotel.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.hotel.dto.request.ReservationRequest;
import com.example.hotel.dto.response.ReservationResponse;

public interface ReservationService {

    ReservationResponse createReservation(
            ReservationRequest request,
            String userEmail);

    Page<ReservationResponse> getReservationsForUser(
            String userEmail,
            Pageable pageable);

    Page<ReservationResponse> getAllReservations(
            Pageable pageable);
}