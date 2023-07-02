package ru.clevertec.authservice.security.jwt.util;

import ru.clevertec.exceptionhandlingstarter.exception.JwtNotFoundException;

import java.util.Objects;

public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer";

    public static String extractJwt(String authorizationHeader) {
        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new JwtNotFoundException("Jwt not found");
        }

        return authorizationHeader.substring(BEARER_PREFIX.length() + 1);
    }
}
