package ru.clevertec.authservice.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.clevertec.authservice.dto.request.LogInRequest;
import ru.clevertec.authservice.dto.request.SignUpRequest;
import ru.clevertec.authservice.dto.response.JwtResponse;
import ru.clevertec.authservice.dto.response.UserResponse;
import ru.clevertec.authservice.entity.User;
import ru.clevertec.authservice.mapper.AuthorityMapper;
import ru.clevertec.authservice.mapper.UserMapper;
import ru.clevertec.authservice.repository.UserRepository;
import ru.clevertec.authservice.security.jwt.impl.JwtProviderImpl;
import ru.clevertec.authservice.security.jwt.util.JwtUtil;
import ru.clevertec.authservice.service.AuthenticationService;
import ru.clevertec.exceptionhandlingstarter.exception.InvalidJwtException;
import ru.clevertec.exceptionhandlingstarter.exception.UserAlreadyExistsException;

import java.util.List;

/**
 * Class implementing {@link AuthenticationService} that provides methods for authorization,
 * registration and JSON Web Token verification
 *
 * @author Ruslan Katnsevich
 * */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthorityMapper authorityMapper;
    private final JwtProviderImpl jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Authorizes the user in the application and returns an access JSON Web Token to him
     *
     * @param logInRequest object of type {@link LogInRequest} containing information about the user to login
     * @return object of type {@link JwtResponse} containing access JSON Web Token
     * */
    @Override
    public JwtResponse logIn(LogInRequest logInRequest) {
        Authentication authentication = authenticate(logInRequest.getUsername(), logInRequest.getPassword());
        String jwt = jwtTokenProvider.generateToken(authentication);

        return JwtResponse.builder()
                .accessToken(jwt)
                .build();
    }

    /**
     * Registers the user in the application and returns an access JSON Web Token to him
     *
     * @param signUpRequest object of type {@link LogInRequest} containing information about the user to signup
     * @return object of type {@link JwtResponse} containing access JSON Web Token
     * */
    @Override
    public JwtResponse signUp(SignUpRequest signUpRequest) {
        userRepository.findByUsername(signUpRequest.getUsername())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User with username = [%s] already exists".formatted(signUpRequest.getUsername()));
                });

        User user = userMapper.mapToUser(signUpRequest);
        userRepository.save(user);

        Authentication authentication = authenticate(signUpRequest.getUsername(), signUpRequest.getPassword());
        String jwt = jwtTokenProvider.generateToken(authentication);

        return JwtResponse.builder()
                .accessToken(jwt)
                .build();
    }

    /**
     * Validates the JSON Web Token. If the token is valid then it returns information about
     * the user authorized (username and authorities) in the application
     *
     * @param authorizationHeader authorization header of HTTP request
     * @return object of type {@link UserResponse} containing information about username and authorities of user
     * */
    @Override
    public UserResponse validateToken(String authorizationHeader) {
        String jwtToken = JwtUtil.extractJwt(authorizationHeader);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!jwtTokenProvider.isTokenValid(jwtToken, authentication)) {
            throw new InvalidJwtException("Jwt token is not valid");
        }

        List<String> authorities = authorityMapper.mapToAuthorities(authentication.getAuthorities());

        return UserResponse.builder()
                .username(authentication.getName())
                .authorities(authorities)
                .build();
    }

    /**
     * Authenticates user in the application
     *
     * @param username username for authentication
     * @param password password for authentication
     * @return object of type {@link Authentication} containing information about the authenticated user
     * */
    private Authentication authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }
}
