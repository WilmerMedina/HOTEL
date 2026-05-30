package com.example.hotel.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hotel.dto.request.ReservationRequest;
import com.example.hotel.dto.response.ReservationResponse;
import com.example.hotel.entity.Reservation;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.User;
import com.example.hotel.enums.ReservationStatus;
import com.example.hotel.exception.ReservationConflictException;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.ReservationRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.UserRepository;
import com.example.hotel.service.ReservationService;

@Service
public class ReservationServiceImpl
        implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            RoomRepository roomRepository) {

        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public ReservationResponse createReservation(
            ReservationRequest request) {

        User user = userRepository.findById(
                request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado"));

        Room room = roomRepository.findById(
                request.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Habitación no encontrada"));

      
        boolean hasConflict = reservationRepository.existsByRoomIdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                room.getId(),
                request.getCheckOut(),
                request.getCheckIn());

        if (hasConflict) {
            throw new ReservationConflictException(
                    "La habitación ya está reservada en esas fechas");
        }

        Reservation reservation = new Reservation();

        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());
        reservation.setTotalPrice(request.getTotalPrice());

        reservation.setStatus(
                ReservationStatus.PENDING);

        reservation.setUser(user);
        reservation.setRoom(room);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        ReservationResponse response =
                new ReservationResponse();

        response.setId(savedReservation.getId());

        response.setCheckIn(
                savedReservation.getCheckIn());

        response.setCheckOut(
                savedReservation.getCheckOut());

        response.setTotalPrice(
                savedReservation.getTotalPrice());

        response.setStatus(
                savedReservation.getStatus());

        return response;
    }

    @Override
    public List<ReservationResponse> getReservations() {

        List<Reservation> reservations =
                reservationRepository.findAll();

        return reservations.stream().map(reservation -> {

            ReservationResponse response =
                    new ReservationResponse();

            response.setId(reservation.getId());
            response.setCheckIn(reservation.getCheckIn());
            response.setCheckOut(reservation.getCheckOut());
            response.setTotalPrice(
                    reservation.getTotalPrice());

            response.setStatus(
                    reservation.getStatus());

            return response;

        }).toList();
    }
}