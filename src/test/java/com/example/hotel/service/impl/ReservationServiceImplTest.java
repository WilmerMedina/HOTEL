
package com.example.hotel.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.hotel.dto.request.ReservationRequest;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.entity.Reservation;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.User;
import com.example.hotel.enums.ReservationStatus;
import com.example.hotel.exception.ReservationConflictException;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void shouldCreateReservation() {

        ReservationRequest request = new ReservationRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2026, 9, 10));
        request.setCheckOut(LocalDate.of(2026, 9, 12));

        String userEmail = "wilmer@gmail.com";

        User user = new User();
        user.setName("Wilmer");
        user.setEmail(userEmail);

        Room room = new Room();
        room.setId(1L);
        room.setPrice(50.0);

        Reservation reservation = new Reservation();

        Reservation savedReservation = new Reservation();
        savedReservation.setId(1L);

        ReservationResponse response = new ReservationResponse();

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(reservationRepository
                .existsByRoomIdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        1L,
                        request.getCheckOut(),
                        request.getCheckIn()))
                .thenReturn(false);

        when(reservationMapper.toEntity(request))
                .thenReturn(reservation);

        when(reservationRepository.save(reservation))
                .thenReturn(savedReservation);

        when(reservationMapper.toResponse(savedReservation))
                .thenReturn(response);

        ReservationResponse result = reservationService.createReservation(request, userEmail);

        assertEquals(response, result);

        assertEquals(user, reservation.getUser());
        assertEquals(room, reservation.getRoom());
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertEquals(100.0, reservation.getTotalPrice());

        verify(userRepository).findByEmail(userEmail);
        verify(roomRepository).findById(1L);
        verify(reservationRepository)
                .existsByRoomIdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        1L,
                        request.getCheckOut(),
                        request.getCheckIn());

        verify(reservationMapper).toEntity(request);
        verify(reservationRepository).save(reservation);
        verify(reservationMapper).toResponse(savedReservation);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        ReservationRequest request = new ReservationRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2026, 9, 10));
        request.setCheckOut(LocalDate.of(2026, 9, 12));

        String userEmail = "wilmer@gmail.com";

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request, userEmail));

        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void shouldThrowExceptionWhenRoomDoesNotExist() {

        ReservationRequest request = new ReservationRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2026, 9, 10));
        request.setCheckOut(LocalDate.of(2026, 9, 12));

        String userEmail = "wilmer@gmail.com";

        User user = new User();
        user.setName("Wilmer");
        user.setEmail(userEmail);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request, userEmail));

        verify(userRepository).findByEmail(userEmail);
        verify(roomRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDatesAreInvalid() {

        ReservationRequest request = new ReservationRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2026, 9, 10));
        request.setCheckOut(LocalDate.of(2026, 9, 10));

        String userEmail = "wilmer@gmail.com";

        User user = new User();
        user.setName("Wilmer");
        user.setEmail(userEmail);

        Room room = new Room();
        room.setId(1L);
        room.setPrice(50.0);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        assertThrows(
                ReservationConflictException.class,
                () -> reservationService.createReservation(request, userEmail));

        verify(userRepository).findByEmail(userEmail);
        verify(roomRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenRoomIsAlreadyReserved() {

        ReservationRequest request = new ReservationRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2026, 9, 10));
        request.setCheckOut(LocalDate.of(2026, 9, 12));

        String userEmail = "wilmer@gmail.com";

        User user = new User();
        user.setName("Wilmer");
        user.setEmail(userEmail);

        Room room = new Room();
        room.setId(1L);
        room.setPrice(50.0);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(reservationRepository
                .existsByRoomIdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        1L,
                        request.getCheckOut(),
                        request.getCheckIn()))
                .thenReturn(true);

        assertThrows(
                ReservationConflictException.class,
                () -> reservationService.createReservation(request, userEmail));

        verify(userRepository).findByEmail(userEmail);
        verify(roomRepository).findById(1L);

        verify(reservationRepository)
                .existsByRoomIdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        1L,
                        request.getCheckOut(),
                        request.getCheckIn());
    }

    @Test
    void shouldReturnReservationsForUser() {

        String userEmail = "wilmer@gmail.com";

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();

        ReservationResponse response1 = new ReservationResponse();
        ReservationResponse response2 = new ReservationResponse();

        Pageable pageable = Pageable.ofSize(10);

        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation1, reservation2));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(reservationRepository.findByUserId(1L, pageable))
                .thenReturn(reservationPage);

        when(reservationMapper.toResponse(reservation1))
                .thenReturn(response1);

        when(reservationMapper.toResponse(reservation2))
                .thenReturn(response2);

        Page<ReservationResponse> result = reservationService.getReservationsForUser(userEmail, pageable);

        assertEquals(2, result.getContent().size());

        assertEquals(response1, result.getContent().get(0));
        assertEquals(response2, result.getContent().get(1));

        verify(userRepository).findByEmail(userEmail);
        verify(reservationRepository).findByUserId(1L, pageable);
        verify(reservationMapper).toResponse(reservation1);
        verify(reservationMapper).toResponse(reservation2);
    }

    @Test
    void shouldReturnEmptyPageWhenUserHasNoReservations() {

        String userEmail = "wilmer@gmail.com";

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        Pageable pageable = Pageable.ofSize(10);

        Page<Reservation> emptyPage = new PageImpl<>(List.of());

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(reservationRepository.findByUserId(1L, pageable))
                .thenReturn(emptyPage);

        Page<ReservationResponse> result = reservationService.getReservationsForUser(userEmail, pageable);

        assertEquals(0, result.getContent().size());

        verify(userRepository).findByEmail(userEmail);
        verify(reservationRepository).findByUserId(1L, pageable);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistWhenGettingReservations() {

        String userEmail = "wilmer@gmail.com";

        Pageable pageable = Pageable.ofSize(10);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.getReservationsForUser(userEmail, pageable));

        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void shouldReturnAllReservations() {

        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();

        ReservationResponse response1 = new ReservationResponse();
        ReservationResponse response2 = new ReservationResponse();

        Pageable pageable = Pageable.ofSize(10);

        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation1, reservation2));

        when(reservationRepository.findAll(pageable))
                .thenReturn(reservationPage);

        when(reservationMapper.toResponse(reservation1))
                .thenReturn(response1);

        when(reservationMapper.toResponse(reservation2))
                .thenReturn(response2);

        Page<ReservationResponse> result = reservationService.getAllReservations(pageable);

        assertEquals(2, result.getContent().size());

        assertEquals(response1, result.getContent().get(0));
        assertEquals(response2, result.getContent().get(1));

        verify(reservationRepository).findAll(pageable);
        verify(reservationMapper).toResponse(reservation1);
        verify(reservationMapper).toResponse(reservation2);
    }

    @Test
    void shouldReturnEmptyPageWhenThereAreNoReservations() {

        Pageable pageable = Pageable.ofSize(10);

        Page<Reservation> emptyPage = new PageImpl<>(List.of());

        when(reservationRepository.findAll(pageable))
                .thenReturn(emptyPage);

        Page<ReservationResponse> result = reservationService.getAllReservations(pageable);

        assertEquals(0, result.getContent().size());

        verify(reservationRepository).findAll(pageable);
    }

}
