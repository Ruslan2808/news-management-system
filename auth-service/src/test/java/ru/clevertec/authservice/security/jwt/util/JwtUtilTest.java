package ru.clevertec.authservice.security.jwt.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ru.clevertec.authservice.util.jwt.JwtTestData;
import ru.clevertec.exceptionhandlingstarter.exception.JwtNotFoundException;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    @Test
    void checkExtractJwtShouldReturnJwt() throws IOException {
        String expectedJwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();
        String authorizationHeader = "Bearer " + expectedJwt;

        String actualJwt = JwtUtil.extractJwt(authorizationHeader);

        assertThat(actualJwt).isEqualTo(expectedJwt);
    }

    @ParameterizedTest
    @MethodSource("provideNotValidAuthorizationHeader")
    void checkExtractJwtShouldThrowsJwtNotFoundException(String authorizationHeader) {
        assertThatThrownBy(() -> JwtUtil.extractJwt(authorizationHeader))
                .isInstanceOf(JwtNotFoundException.class);
    }

    private static Stream<String> provideNotValidAuthorizationHeader() {
        return Stream.of(
                null,
                "Jwt eyJ0eXAiOi",
                "bearereyJ0eXAiOi",
                "bearer eyJ0eXAiOi",
                "BEARER eyJ0eXAiOi"
        );
    }
}
