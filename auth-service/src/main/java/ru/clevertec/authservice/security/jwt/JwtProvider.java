package ru.clevertec.authservice.security.jwt;

import org.springframework.security.core.Authentication;

public interface JwtProvider {

    String generateToken(Authentication authentication);
    boolean isTokenValid(String jwt, Authentication authentication);
    boolean isTokenExpired(String jwt);

}
