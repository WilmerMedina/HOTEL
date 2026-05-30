package com.example.hotel.service;

import java.util.List;

import com.example.hotel.dto.request.ReservationRequest;
import com.example.hotel.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse createReservation(
            ReservationRequest request);

    List<ReservationResponse> getReservations();
}
