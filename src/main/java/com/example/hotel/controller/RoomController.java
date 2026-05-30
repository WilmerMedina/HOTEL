package com.example.hotel.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.service.RoomService;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomResponse> getAllRooms() {
        return roomService.getAllRooms();
    }

    @PostMapping
    public RoomResponse createRoom(
            @RequestBody RoomRequest request) {

        return roomService.createRoom(request);
    }

    @PutMapping("/{id}")
    public RoomResponse updateRoom(
            @PathVariable Long id,
            @RequestBody RoomRequest request) {

        return roomService.updateRoom(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }
}