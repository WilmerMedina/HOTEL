
package com.example.hotel.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.entity.Room;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.mapper.RoomMapper;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.service.RoomService;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomServiceImpl(
            RoomRepository roomRepository,
            RoomMapper roomMapper) {

        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
    }

    @Override
    public List<RoomResponse> getAllRooms() {

        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    public RoomResponse createRoom(RoomRequest request) {

        Room room = roomMapper.toEntity(request);

        Room savedRoom = roomRepository.save(room);

        return roomMapper.toResponse(savedRoom);
    }

    @Override
    public RoomResponse updateRoom(
            Long id,
            RoomRequest request) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Habitación no encontrada"
                        ));

        roomMapper.updateEntity(room, request);

        Room updatedRoom = roomRepository.save(room);

        return roomMapper.toResponse(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Habitación no encontrada"
                        ));

        roomRepository.delete(room);
    }
}

