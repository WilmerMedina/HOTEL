
package com.example.hotel.mapper;

import org.springframework.stereotype.Component;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.entity.Room;

@Component
public class RoomMapper {

    public RoomResponse toResponse(Room room) {

        RoomResponse response = new RoomResponse();

        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setPrice(room.getPrice());
        response.setCapacity(room.getCapacity());
        response.setType(room.getType());
        response.setStatus(room.getStatus());

        return response;
    }

    public Room toEntity(RoomRequest request) {

        Room room = new Room();

        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setCapacity(request.getCapacity());
        room.setType(request.getType());
        room.setStatus(request.getStatus());

        return room;
    }

    public void updateEntity(Room room, RoomRequest request) {

        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setCapacity(request.getCapacity());
        room.setType(request.getType());
        room.setStatus(request.getStatus());
    }


}
