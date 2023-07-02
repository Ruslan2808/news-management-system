package ru.clevertec.exceptionhandlingstarter.exception;

public class JwtNotFoundException extends RuntimeException {

    public JwtNotFoundException(String message) {
        super(message);
    }
}
