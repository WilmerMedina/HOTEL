package com.example.hotel.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.hotel.dto.request.ReservationRequest;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        ReservationResponse created = reservationService.createReservation(request, userEmail);

        log.info("Reserva creada (id={}) por usuario '{}'",
                created.getId(), userEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Page<ReservationResponse>> getMyReservations(
            Authentication authentication,
            Pageable pageable) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                reservationService.getReservationsForUser(userEmail, pageable));
    }

    @GetMapping("/list/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            Pageable pageable) {

        return ResponseEntity.ok(
                reservationService.getAllReservations(pageable));
    }
}