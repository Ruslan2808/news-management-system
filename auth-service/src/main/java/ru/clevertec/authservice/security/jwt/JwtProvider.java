package ru.clevertec.authservice.security.jwt;

import org.springframework.security.core.Authentication;

/**
 * Interface for performing operations with JSON Web Token
 *
 * @author Ruslan Katnsevich
 * */
public interface JwtProvider {

    String generateToken(Authentication authentication);
    boolean isTokenValid(String jwt, Authentication authentication);
    boolean isTokenExpired(String jwt);

}
