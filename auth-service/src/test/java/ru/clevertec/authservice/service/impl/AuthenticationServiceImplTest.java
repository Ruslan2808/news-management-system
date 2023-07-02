package ru.clevertec.authservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.clevertec.authservice.dto.request.LogInRequest;
import ru.clevertec.authservice.dto.request.SignUpRequest;
import ru.clevertec.authservice.dto.response.JwtResponse;
import ru.clevertec.authservice.dto.response.UserResponse;
import ru.clevertec.authservice.entity.User;
import ru.clevertec.authservice.mapper.AuthorityMapper;
import ru.clevertec.authservice.mapper.UserMapper;
import ru.clevertec.authservice.repository.UserRepository;
import ru.clevertec.authservice.security.jwt.impl.JwtProviderImpl;
import ru.clevertec.authservice.util.entity.UserTestBuilder;
import ru.clevertec.authservice.util.factory.SecurityContextFactory;
import ru.clevertec.authservice.util.jwt.JwtTestData;
import ru.clevertec.authservice.util.request.LogInRequestTestBuilder;
import ru.clevertec.authservice.util.request.SignUpRequestTestBuilder;
import ru.clevertec.authservice.util.response.JwtResponseTestBuilder;
import ru.clevertec.authservice.util.response.UserResponseTestBuilder;
import ru.clevertec.exceptionhandlingstarter.exception.InvalidJwtException;
import ru.clevertec.exceptionhandlingstarter.exception.UserAlreadyExistsException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityMapper authorityMapper;

    @Mock
    private JwtProviderImpl jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        String username = "erik_gibson";
        List<String> roles = List.of("ADMIN");
        SecurityContextFactory.buildSecurityContext(username, roles);
    }

    @Nested
    class AuthenticationServiceImplLogInTest {

        @Test
        void checkLogInShouldReturnJwtResponse() throws IOException {
            String username = "erik_gibson";
            String password = "password";
            Authentication authenticationToken = new TestingAuthenticationToken(username, password);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String jwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();
            LogInRequest logInRequest = LogInRequestTestBuilder.logInRequest()
                    .withUsername(username)
                    .withPassword(password)
                    .build();
            JwtResponse expectedJwtResponse = JwtResponseTestBuilder.jwtResponse()
                    .withAccessToken(jwt)
                    .build();

            doReturn(authentication).when(authenticationManager).authenticate(authenticationToken);
            doReturn(jwt).when(jwtTokenProvider).generateToken(authentication);

            JwtResponse actualJwtResponse = authenticationService.logIn(logInRequest);

            assertThat(actualJwtResponse).isEqualTo(expectedJwtResponse);
        }
    }

    @Nested
    class AuthenticationServiceImplSgnUpTest {

        @Test
        void checkSignUpShouldReturnJwtResponse() throws IOException {
            String username = "erik_gibson";
            String password = "password";
            Authentication authenticationToken = new TestingAuthenticationToken(username, password);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String jwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();
            SignUpRequest signUpRequest = SignUpRequestTestBuilder.signUpRequest()
                    .withUsername(username)
                    .withPassword(password)
                    .build();
            User user = UserTestBuilder.user()
                    .withUsername(username)
                    .withPassword(password)
                    .build();
            JwtResponse expectedJwtResponse = JwtResponseTestBuilder.jwtResponse()
                    .withAccessToken(jwt)
                    .build();

            doReturn(Optional.empty()).when(userRepository).findByUsername(username);
            doReturn(user).when(userMapper).mapToUser(signUpRequest);
            doReturn(authentication).when(authenticationManager).authenticate(authenticationToken);
            doReturn(jwt).when(jwtTokenProvider).generateToken(authentication);

            JwtResponse actualJwtResponse = authenticationService.signUp(signUpRequest);

            assertThat(actualJwtResponse).isEqualTo(expectedJwtResponse);
        }

        @Test
        void checkSignUpShouldThrowsUserAlreadyExistsException() {
            String username = "ivan_ivanov";
            String password = "password";
            SignUpRequest signUpRequest = SignUpRequestTestBuilder.signUpRequest()
                    .withUsername(username)
                    .withPassword(password)
                    .build();
            User user = UserTestBuilder.user()
                    .withUsername(username)
                    .withPassword(password)
                    .build();

            doReturn(Optional.of(user)).when(userRepository).findByUsername(username);

            assertThatThrownBy(() -> authenticationService.signUp(signUpRequest))
                    .isInstanceOf(UserAlreadyExistsException.class);
        }
    }

    @Nested
    class AuthenticationServiceImplValidateTokenTest {

        @Test
        void checkValidateTokenShouldReturnUserResponse() throws IOException {
            String validJwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();
            String authorizationHeader = "Bearer %s".formatted(validJwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<String> authorities = List.of("ADMIN");
            UserResponse expectedUserResponse = UserResponseTestBuilder.userResponse()
                    .withUsername(authentication.getName())
                    .withAuthorities(authorities)
                    .build();

            doReturn(true).when(jwtTokenProvider).isTokenValid(validJwt, authentication);
            doReturn(authorities).when(authorityMapper).mapToAuthorities(authentication.getAuthorities());

            UserResponse actualUserResponse = authenticationService.validateToken(authorizationHeader);

            assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
        }

        @Test
        void checkValidateTokenShouldThrowsInvalidJwtException() throws IOException {
            String expiredJwt = JwtTestData.buildErikGibsonExpiredJwt().getAccessToken();
            String authorizationHeader = "Bearer %s".formatted(expiredJwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            doReturn(false).when(jwtTokenProvider).isTokenValid(expiredJwt, authentication);

            assertThatThrownBy(() -> authenticationService.validateToken(authorizationHeader))
                    .isInstanceOf(InvalidJwtException.class);
        }
    }
}
