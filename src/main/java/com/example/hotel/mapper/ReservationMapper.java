package com.example.hotel.mapper;

import org.springframework.stereotype.Component;

import com.example.hotel.dto.request.ReservationRequest;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.entity.Reservation;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {

        ReservationResponse response = new ReservationResponse();

        response.setId(reservation.getId());
        response.setCheckIn(reservation.getCheckIn());
        response.setCheckOut(reservation.getCheckOut());
        response.setTotalPrice(reservation.getTotalPrice());
        response.setStatus(reservation.getStatus());

        return response;
    }

    public Reservation toEntity(ReservationRequest request) {

        Reservation reservation = new Reservation();

        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());


        return reservation;
    }

    public void updateEntity(Reservation reservation, ReservationRequest request) {

        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());

    }
}