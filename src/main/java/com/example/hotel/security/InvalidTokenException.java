package com.example.hotel.security;

/**
 * Se lanza cuando un JWT es inválido, está expirado o fue manipulado.
 * Permite diferenciar este caso de errores inesperados (bugs, fallos de BD)
 * en el filtro de autenticación.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}