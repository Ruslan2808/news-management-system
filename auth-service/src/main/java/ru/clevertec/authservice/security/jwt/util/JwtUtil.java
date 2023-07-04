package ru.clevertec.authservice.security.jwt.util;

import ru.clevertec.exceptionhandlingstarter.exception.JwtNotFoundException;

import java.util.Objects;

/**
 * Utility class that provides methods for working with JSON Web Token
 *
 * @author Ruslan Kantsevich
 * */
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer";

    /**
     * Extracts JSON Web Token from authorization header of HTTP request
     *
     * @param authorizationHeader authorization header of HTTP request
     * @return JSON Web Token
     * */
    public static String extractJwt(String authorizationHeader) {
        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new JwtNotFoundException("Jwt not found");
        }

        return authorizationHeader.substring(BEARER_PREFIX.length() + 1);
    }
}
