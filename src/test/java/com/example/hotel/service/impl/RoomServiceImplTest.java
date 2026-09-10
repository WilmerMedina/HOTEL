
package com.example.hotel.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.hotel.dto.request.RoomRequest;
import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.entity.Room;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.mapper.RoomMapper;
import com.example.hotel.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    void shouldReturnRoomById() {

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setPrice(50.0);
        room.setCapacity(2);

        RoomResponse response = new RoomResponse();
        response.setId(1L);
        response.setRoomNumber("101");
        response.setPrice(50.0);
        response.setCapacity(2);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(roomMapper.toResponse(room))
                .thenReturn(response);

        RoomResponse result = roomService.getRoomById(1L);

        assertEquals(1L, result.getId());
        assertEquals("101", result.getRoomNumber());
        assertEquals(50.0, result.getPrice());
        assertEquals(2, result.getCapacity());
    }

    @Test
    void shouldThrowExceptionWhenRoomDoesNotExist() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.getRoomById(1L));
    }

    @Test
    void shouldCreateRoom() {

        RoomRequest request = new RoomRequest();
        request.setRoomNumber("101");
        request.setPrice(50.0);
        request.setCapacity(2);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setPrice(50.0);
        room.setCapacity(2);

        RoomResponse response = new RoomResponse();
        response.setId(1L);
        response.setRoomNumber("101");
        response.setPrice(50.0);
        response.setCapacity(2);

        when(roomMapper.toEntity(request))
                .thenReturn(room);

        when(roomRepository.save(room))
                .thenReturn(room);

        when(roomMapper.toResponse(room))
                .thenReturn(response);

        RoomResponse result = roomService.createRoom(request);

        assertEquals(1L, result.getId());
        assertEquals("101", result.getRoomNumber());
        assertEquals(50.0, result.getPrice());
        assertEquals(2, result.getCapacity());
    }

    @Test
    void shouldUpdateRoom() {

        Long roomId = 1L;

        RoomRequest request = new RoomRequest();
        request.setRoomNumber("102");
        request.setPrice(80.0);
        request.setCapacity(3);

        Room room = new Room();
        room.setId(roomId);
        room.setRoomNumber("101");
        room.setPrice(50.0);
        room.setCapacity(2);

        RoomResponse response = new RoomResponse();
        response.setId(roomId);
        response.setRoomNumber("102");
        response.setPrice(80.0);
        response.setCapacity(3);

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.of(room));

        when(roomRepository.save(room))
                .thenReturn(room);

        when(roomMapper.toResponse(room))
                .thenReturn(response);

        RoomResponse result = roomService.updateRoom(roomId, request);

        assertEquals(1L, result.getId());
        assertEquals("102", result.getRoomNumber());
        assertEquals(80.0, result.getPrice());
        assertEquals(3, result.getCapacity());

        verify(roomRepository).findById(roomId);
        verify(roomMapper).updateEntity(room, request);
        verify(roomRepository).save(room);
        verify(roomMapper).toResponse(room);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRoomDoesNotExist() {

        Long roomId = 1L;

        RoomRequest request = new RoomRequest();

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.updateRoom(roomId, request));
    }

    @Test
    void shouldDeleteRoom() {

        Long roomId = 1L;

        Room room = new Room();
        room.setId(roomId);
        room.setRoomNumber("101");

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.of(room));

        roomService.deleteRoom(roomId);

        verify(roomRepository).findById(roomId);
        verify(roomRepository).delete(room);
    }

    @Test
    void shouldThrowExceptionWhenDeletingRoomDoesNotExist() {

        Long roomId = 1L;

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.deleteRoom(roomId));

        verify(roomRepository).findById(roomId);
    }

    @Test
    void shouldReturnAllRooms() {

        PageRequest pageable = PageRequest.of(0, 10);

        Room room1 = new Room();
        room1.setId(1L);
        room1.setRoomNumber("101");

        Room room2 = new Room();
        room2.setId(2L);
        room2.setRoomNumber("102");

        Page<Room> roomPage = new PageImpl<>(List.of(room1, room2));

        RoomResponse response1 = new RoomResponse();
        response1.setId(1L);
        response1.setRoomNumber("101");

        RoomResponse response2 = new RoomResponse();
        response2.setId(2L);
        response2.setRoomNumber("102");

        when(roomRepository.findAll(pageable))
                .thenReturn(roomPage);

        when(roomMapper.toResponse(room1))
                .thenReturn(response1);

        when(roomMapper.toResponse(room2))
                .thenReturn(response2);

        Page<RoomResponse> result = roomService.getAllRooms(pageable);


        assertEquals(2, result.getContent().size());
        assertEquals("101", result.getContent().get(0).getRoomNumber());
        assertEquals("102", result.getContent().get(1).getRoomNumber());

        verify(roomRepository).findAll(pageable);
        verify(roomMapper).toResponse(room1);
        verify(roomMapper).toResponse(room2);
    }

}
