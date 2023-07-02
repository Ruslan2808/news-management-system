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

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthorityMapper authorityMapper;
    private final JwtProviderImpl jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponse logIn(LogInRequest logInRequest) {
        Authentication authentication = authenticate(logInRequest.getUsername(), logInRequest.getPassword());
        String jwt = jwtTokenProvider.generateToken(authentication);

        return JwtResponse.builder()
                .accessToken(jwt)
                .build();
    }

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

    private Authentication authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }
}
