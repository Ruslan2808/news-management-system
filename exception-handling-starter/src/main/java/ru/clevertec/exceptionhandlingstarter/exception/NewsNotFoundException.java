package ru.clevertec.exceptionhandlingstarter.exception;

public class NewsNotFoundException extends RuntimeException {

    public NewsNotFoundException(String message) {
        super(message);
    }
}
