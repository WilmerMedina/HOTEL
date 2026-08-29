package com.example.hotel.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(
                        ResourceNotFoundException ex,
                        HttpServletRequest request) {

                log.debug("Recurso no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "NOT_FOUND",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(error);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthentication(
                        AuthenticationException ex,
                        HttpServletRequest request) {

                // Nivel WARN: intentos de autenticación fallidos son relevantes para
                // detectar fuerza bruta o abuso. Se loguea la IP, nunca credenciales.
                log.warn("Fallo de autenticación desde IP {} en {}: {}",
                                request.getRemoteAddr(), request.getRequestURI(), ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "UNAUTHORIZED",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(error);
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErrorResponse> handleConflict(
                        ConflictException ex,
                        HttpServletRequest request) {

                log.debug("Conflicto en {}: {}", request.getRequestURI(), ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "CONFLICT",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        @ExceptionHandler(ReservationConflictException.class)
        public ResponseEntity<ErrorResponse> handleReservationConflict(
                        ReservationConflictException ex,
                        HttpServletRequest request) {

                log.debug("Conflicto de reserva en {}: {}", request.getRequestURI(), ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "RESERVATION_CONFLICT",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                List<String> details = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": "
                                                + error.getDefaultMessage())
                                .toList();

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "VALIDATION_ERROR",
                                "Error de validación",
                                request.getRequestURI(),
                                details);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleMalformedJson(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {

                log.debug("Body malformado en {}: {}", request.getRequestURI(), ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "MALFORMED_REQUEST",
                                "El cuerpo de la petición no es un JSON válido",
                                request.getRequestURI(),
                                List.of());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        @ExceptionHandler(AuthorizationDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(
                        AuthorizationDeniedException ex,
                        HttpServletRequest request) {

                // Nivel WARN: intentos de acceso a recursos sin permiso son relevantes
                // para detectar escalación de privilegios o exploración maliciosa.
                log.warn("Acceso denegado para usuario en {} desde IP {}",
                                request.getRequestURI(), request.getRemoteAddr());

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.FORBIDDEN.value(),
                                "FORBIDDEN",
                                "No tienes permisos para realizar esta acción",
                                request.getRequestURI(),
                                List.of());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneral(
                        Exception ex,
                        HttpServletRequest request) {

                // Nivel ERROR con stack trace completo: esto es lo único que te
                // permite diagnosticar bugs reales en producción. El cliente nunca
                // ve este detalle (mensaje genérico abajo), pero en tus logs sí
                // debe quedar completo.
                log.error("Error inesperado procesando {} {}",
                                request.getMethod(), request.getRequestURI(), ex);

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "INTERNAL_SERVER_ERROR",
                                "Ocurrió un error inesperado",
                                request.getRequestURI(),
                                List.of());

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(error);
        }
}