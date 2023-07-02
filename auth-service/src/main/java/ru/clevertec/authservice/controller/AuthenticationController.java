package ru.clevertec.authservice.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.clevertec.authservice.dto.request.LogInRequest;
import ru.clevertec.authservice.dto.request.SignUpRequest;
import ru.clevertec.authservice.dto.response.JwtResponse;
import ru.clevertec.authservice.dto.response.UserResponse;
import ru.clevertec.authservice.service.impl.AuthenticationServiceImpl;
import ru.clevertec.loggingstarter.annotation.Loggable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Loggable
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> logIn(@RequestBody LogInRequest logInRequest) {
        JwtResponse jwtResponse = authenticationService.logIn(logInRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("signup")
    public ResponseEntity<JwtResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        JwtResponse jwtResponse = authenticationService.signUp(signUpRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("validate")
    public ResponseEntity<UserResponse> validateToken(@RequestHeader(value = AUTHORIZATION, required = false) String authorizationHeader) {
        UserResponse userResponse = authenticationService.validateToken(authorizationHeader);
        return ResponseEntity.ok(userResponse);
    }
}
