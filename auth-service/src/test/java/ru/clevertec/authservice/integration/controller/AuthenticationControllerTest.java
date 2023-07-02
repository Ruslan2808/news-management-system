package ru.clevertec.authservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import ru.clevertec.authservice.dto.request.LogInRequest;
import ru.clevertec.authservice.dto.request.SignUpRequest;
import ru.clevertec.authservice.dto.response.UserResponse;
import ru.clevertec.authservice.integration.BaseIntegrationTest;
import ru.clevertec.authservice.util.jwt.JwtTestData;
import ru.clevertec.authservice.util.request.LogInRequestTestBuilder;
import ru.clevertec.authservice.util.request.SignUpRequestTestBuilder;
import ru.clevertec.authservice.util.response.UserResponseTestBuilder;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthenticationControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class AuthenticationLogInTest {

        @ParameterizedTest
        @MethodSource("provideLogInRequests")
        void checkLogInShouldReturnStatusOkAndReturnJwtResponse(LogInRequest logInRequest) throws Exception {
            String logInRequestBody = objectMapper.writeValueAsString(logInRequest);

            mockMvc.perform(post("/api/v1/auth/login")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(logInRequestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty());
        }

        @ParameterizedTest
        @MethodSource("provideBadLogInRequests")
        void checkLogInShouldReturnStatusUnauthorizedAndMessageBadCredentials(LogInRequest logInRequest) throws Exception {
            String logInRequestBody = objectMapper.writeValueAsString(logInRequest);
            String expectedMessage = "Bad credentials";

            mockMvc.perform(post("/api/v1/auth/login")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(logInRequestBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<LogInRequest> provideLogInRequests() {
            return Stream.of(
                    LogInRequestTestBuilder.logInRequest().withUsername("erik_gibson").withPassword("password").build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("thomas_martinez").withPassword("password").build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("sharon_hill").withPassword("password").build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("laura_norton").withPassword("password").build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("debbie_garcia").withPassword("password").build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("ronnie_stevens").withPassword("password").build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("tammy_simmons").withPassword("password").build()
            );
        }

        private static Stream<LogInRequest> provideBadLogInRequests() {
            return Stream.of(
                    LogInRequestTestBuilder.logInRequest().withUsername(null).withPassword(null).build(),
                    LogInRequestTestBuilder.logInRequest().withUsername(null).withPassword("password").build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("erik_gibson").withPassword(null).build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("ivanov").withPassword("password").build(),
                    LogInRequestTestBuilder.logInRequest().withUsername("erik_gibson").withPassword("Password1234").build()
            );
        }
    }

    @Nested
    class AuthenticationSignUpTest {

        @Test
        void checkSignUpShouldReturnStatusOkAndReturnJwtResponse() throws Exception {
            SignUpRequest signUpRequest = SignUpRequestTestBuilder.signUpRequest()
                    .withUsername("ivanov_ivanov")
                    .withPassword("password")
                    .build();
            String signUpRequestBody = objectMapper.writeValueAsString(signUpRequest);

            mockMvc.perform(post("/api/v1/auth/signup")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(signUpRequestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty());
        }

        @ParameterizedTest
        @MethodSource("provideNotValidSignUptRequestsAndExpectedMessages")
        void checkSignUpShouldReturnStatusBadRequestsAndReturnMessageBadRequests(SignUpRequest signUpRequest,
                                                                                 String expectedMessage) throws Exception {
            String signUpRequestBody = objectMapper.writeValueAsString(signUpRequest);

            mockMvc.perform(post("/api/v1/auth/signup")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(signUpRequestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideAlreadyExistsUsernames")
        void checkSignUpShouldReturnStatusConflictAndReturnMessageAlreadyExists(String username) throws Exception {
            SignUpRequest signUpRequest = SignUpRequestTestBuilder.signUpRequest()
                    .withUsername(username)
                    .withPassword("password")
                    .build();
            String signUpRequestBody = objectMapper.writeValueAsString(signUpRequest);
            String expectedMessage = "User with username = [%s] already exists".formatted(username);

            mockMvc.perform(post("/api/v1/auth/signup")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(signUpRequestBody))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Arguments> provideNotValidSignUptRequestsAndExpectedMessages() {
            return Stream.of(
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername(null)
                                    .withPassword("password")
                                    .build(),
                            "User username cannot be empty"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername("")
                                    .withPassword("password")
                                    .build(),
                            "User username cannot be empty"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername(" ")
                                    .withPassword("password")
                                    .build(),
                            "User username cannot be empty"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername("ivan_ivanov")
                                    .withPassword(null)
                                    .build(),
                            "User password cannot be empty"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername("ivan_ivanov")
                                    .withPassword("")
                                    .build(),
                            "User password cannot be empty"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername("ivan_ivanov")
                                    .withPassword(" ")
                                    .build(),
                            "User password cannot be empty"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername("ivan_ivanov")
                                    .withPassword("password")
                                    .withRole(null)
                                    .build(),
                            "User role cannot be empty"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername("ivan_ivanov")
                                    .withPassword("password")
                                    .withRole("")
                                    .build(),
                            "User role can only be ADMIN, JOURNALIST OR SUBSCRIBER"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername("ivan_ivanov")
                                    .withPassword("password")
                                    .withRole(" ")
                                    .build(),
                            "User role can only be ADMIN, JOURNALIST OR SUBSCRIBER"
                    ),
                    arguments(
                            SignUpRequestTestBuilder.signUpRequest()
                                    .withUsername("ivan_ivanov")
                                    .withPassword("password")
                                    .withRole("USER")
                                    .build(),
                            "User role can only be ADMIN, JOURNALIST OR SUBSCRIBER"
                    )
            );
        }

        private static Stream<String> provideAlreadyExistsUsernames() {
            return Stream.of(
                    "erik_gibson",
                    "thomas_martinez",
                    "sharon_hill",
                    "laura_norton",
                    "debbie_garcia",
                    "ronnie_stevens",
                    "tammy_simmons"
            );
        }
    }

    @Nested
    class AuthenticationValidateTokenTest {

        @Test
        void checkValidateTokenShouldReturnStatusOkAndUserResponse() throws Exception {
            String validJwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();
            String authorizationHeader = "Bearer " + validJwt;
            UserResponse expectedUserResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("erik_gibson")
                    .withAuthorities(List.of("ROLE_ADMIN"))
                    .build();
            String expectedUserResponseBody = objectMapper.writeValueAsString(expectedUserResponse);

            mockMvc.perform(get("/api/v1/auth/validate")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .header(AUTHORIZATION, authorizationHeader))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedUserResponseBody));
        }

        @ParameterizedTest
        @MethodSource("provideNotValidAuthorizationHeader")
        void checkValidateTokenShouldReturnStatusUnauthorizedAndMessageJwtTokenExpired(String authorizationHeader) throws Exception {
            mockMvc.perform(get("/api/v1/auth/validate")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .header(AUTHORIZATION, authorizationHeader))
                    .andExpect(status().isUnauthorized());
        }

        private static Stream<String> provideNotValidAuthorizationHeader() {
            return Stream.of(
                    "Jwt eyJ0eXAiOi",
                    "bearereyJ0eXAiOi",
                    "bearer eyJ0eXAiOi",
                    "BEARER eyJ0eXAiOi"
            );
        }
    }
}
