package com.example.hotel.service;

import java.util.List;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomResponse;

public interface RoomService {

    List<RoomResponse> getAllRooms();

    RoomResponse createRoom(RoomRequest request);

    RoomResponse updateRoom(Long id, RoomRequest request);

    void deleteRoom(Long id);
}