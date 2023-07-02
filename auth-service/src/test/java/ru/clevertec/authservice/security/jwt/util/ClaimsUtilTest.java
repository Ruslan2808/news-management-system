package ru.clevertec.authservice.security.jwt.util;

import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import ru.clevertec.authservice.util.jwt.JwtTestData;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ClaimsUtilTest {

    @BeforeEach
    void setUp() {
        String jwtSecretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        ReflectionTestUtils.setField(KeyUtil.class, "jwtSecretKey", jwtSecretKey);
    }

    @Test
    void checkExtractClaimShouldReturnSubject() throws IOException {
        String expectedSubject = "erik_gibson";
        String jwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();

        String actualSubject = ClaimsUtil.extractClaim(jwt, Claims::getSubject);

        assertThat(actualSubject).isEqualTo(expectedSubject);
    }

    @Test
    void checkExtractClaimShouldReturnIssuedAt() throws IOException {
        Date expectedIssuedAt = Date.from(Instant.ofEpochSecond(1688311177));
        String jwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();

        Date actualIssuedAt = ClaimsUtil.extractClaim(jwt, Claims::getIssuedAt);

        assertThat(actualIssuedAt).isEqualTo(expectedIssuedAt);
    }

    @Test
    void checkExtractClaimShouldReturnExpiration() throws IOException {
        Date expectedExpiration = Date.from(Instant.ofEpochSecond(1690939177));
        String jwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();

        Date actualExpiration = ClaimsUtil.extractClaim(jwt, Claims::getExpiration);

        assertThat(actualExpiration).isEqualTo(expectedExpiration);
    }
}
