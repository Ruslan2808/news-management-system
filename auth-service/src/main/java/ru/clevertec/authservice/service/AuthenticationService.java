package ru.clevertec.authservice.service;

import ru.clevertec.authservice.dto.request.LogInRequest;
import ru.clevertec.authservice.dto.request.SignUpRequest;
import ru.clevertec.authservice.dto.response.JwtResponse;
import ru.clevertec.authservice.dto.response.UserResponse;

/**
 * Interface for performing operations on authorization, registration and JSON Web Token validation
 *
 * @author Ruslan Katnsevich
 * */
public interface AuthenticationService {

    JwtResponse logIn(LogInRequest logInRequest);
    JwtResponse signUp(SignUpRequest signUpRequest);
    UserResponse validateToken(String authorizationHeader);

}
