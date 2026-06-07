package com.example.hotel.controller;


import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.hotel.dto.request.ReservationRequest;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.service.ReservationService;


@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService) {

        this.reservationService = reservationService;
    }


    @PostMapping("/create")
    public ReservationResponse createReservation(
            @RequestBody ReservationRequest request) {

        return reservationService.createReservation(request);
    }

    @GetMapping("/list")
    public List<ReservationResponse> getReservations() {

        return reservationService.getReservations();
    }
}