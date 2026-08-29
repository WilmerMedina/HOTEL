package com.example.hotel.service.impl;

import java.time.temporal.ChronoUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import com.example.hotel.service.ReservationService;

@Service
public class ReservationServiceImpl implements ReservationService {

        private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

        private final ReservationRepository reservationRepository;
        private final UserRepository userRepository;
        private final RoomRepository roomRepository;
        private final ReservationMapper reservationMapper;

        public ReservationServiceImpl(
                        ReservationRepository reservationRepository,
                        UserRepository userRepository,
                        RoomRepository roomRepository,
                        ReservationMapper reservationMapper) {

                this.reservationRepository = reservationRepository;
                this.userRepository = userRepository;
                this.roomRepository = roomRepository;
                this.reservationMapper = reservationMapper;
        }

        @Override
        public ReservationResponse createReservation(ReservationRequest request, String userEmail) {

                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

                Room room = roomRepository.findById(request.getRoomId())
                                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

                long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());

                if (nights <= 0) {
                        throw new ReservationConflictException(
                                        "La fecha de salida debe ser posterior a la fecha de entrada");
                }

                boolean hasConflict = reservationRepository
                                .existsByRoomIdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                                                room.getId(),
                                                request.getCheckOut(),
                                                request.getCheckIn());

                if (hasConflict) {
                        throw new ReservationConflictException(
                                        "La habitación ya está reservada en esas fechas");
                }

                Reservation reservation = reservationMapper.toEntity(request);
                reservation.setUser(user);
                reservation.setRoom(room);
                reservation.setStatus(ReservationStatus.PENDING);

                reservation.setTotalPrice(room.getPrice() * nights);

                Reservation savedReservation = reservationRepository.save(reservation);

                log.info("Reserva creada (id={}) para usuario '{}' en habitación {}",
                                savedReservation.getId(), userEmail, room.getId());

                return reservationMapper.toResponse(savedReservation);
        }

        @Override
        public Page<ReservationResponse> getReservationsForUser(
                        String userEmail,
                        Pageable pageable) {

                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

                return reservationRepository
                                .findByUserId(user.getId(), pageable)
                                .map(reservationMapper::toResponse);
        }

        @Override
        public Page<ReservationResponse> getAllReservations(Pageable pageable) {

                return reservationRepository.findAll(pageable)
                                .map(reservationMapper::toResponse);
        }
}