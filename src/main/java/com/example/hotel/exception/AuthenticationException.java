package com.example.hotel.exception;

public class AuthenticationException
        extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}