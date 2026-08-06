package com.example.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ReservationResponse>> getReservations() {

        return ResponseEntity.ok(
                reservationService.getReservations());
    }
}