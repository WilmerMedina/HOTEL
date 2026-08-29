package com.example.hotel.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomResponse;

public interface RoomService {

    Page<RoomResponse> getAllRooms(Pageable pageable);

    RoomResponse createRoom(RoomRequest request);

    RoomResponse updateRoom(Long id, RoomRequest request);

    void deleteRoom(Long id);
}