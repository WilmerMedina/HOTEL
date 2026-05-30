package com.example.hotel.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.entity.Room;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.service.RoomService;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<RoomResponse> getAllRooms() {

        List<Room> rooms = roomRepository.findAll();

        return rooms.stream().map(room -> {

            RoomResponse response = new RoomResponse();

            response.setId(room.getId());
            response.setRoomNumber(room.getRoomNumber());
            response.setPrice(room.getPrice());
            response.setCapacity(room.getCapacity());
            response.setType(room.getType());
            response.setStatus(room.getStatus());

            return response;

        }).toList();
    }

    @Override
    public RoomResponse createRoom(RoomRequest request) {

        Room room = new Room();

        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setCapacity(request.getCapacity());
        room.setType(request.getType());
        room.setStatus(request.getStatus());

        Room savedRoom = roomRepository.save(room);

        RoomResponse response = new RoomResponse();

        response.setId(savedRoom.getId());
        response.setRoomNumber(savedRoom.getRoomNumber());
        response.setPrice(savedRoom.getPrice());
        response.setCapacity(savedRoom.getCapacity());
        response.setType(savedRoom.getType());
        response.setStatus(savedRoom.getStatus());

        return response;
    }

    @Override
    public RoomResponse updateRoom(Long id, RoomRequest request) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Habitación no encontrada"
                        ));

        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setCapacity(request.getCapacity());
        room.setType(request.getType());
        room.setStatus(request.getStatus());

        Room updatedRoom = roomRepository.save(room);

        RoomResponse response = new RoomResponse();

        response.setId(updatedRoom.getId());
        response.setRoomNumber(updatedRoom.getRoomNumber());
        response.setPrice(updatedRoom.getPrice());
        response.setCapacity(updatedRoom.getCapacity());
        response.setType(updatedRoom.getType());
        response.setStatus(updatedRoom.getStatus());

        return response;
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