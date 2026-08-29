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

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.service.RoomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Page<RoomResponse>> getAllRooms(
            Pageable pageable) {

        return ResponseEntity.ok(
                roomService.getAllRooms(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody RoomRequest request,
            Authentication authentication) {

        RoomResponse created = roomService.createRoom(request);

        log.info("Habitación creada (id={}) por admin '{}'",
                created.getId(), authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request,
            Authentication authentication) {

        RoomResponse updated = roomService.updateRoom(id, request);

        log.info("Habitación actualizada (id={}) por admin '{}'",
                id, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long id,
            Authentication authentication) {

        roomService.deleteRoom(id);

        log.warn("Habitación eliminada (id={}) por admin '{}'",
                id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}