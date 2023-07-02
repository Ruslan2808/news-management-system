package ru.clevertec.exceptionhandlingstarter.exception;

public class CacheNotDefinedException extends RuntimeException {

    public CacheNotDefinedException(String message) {
        super(message);
    }
}
