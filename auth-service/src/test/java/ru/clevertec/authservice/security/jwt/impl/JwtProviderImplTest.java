package ru.clevertec.authservice.security.jwt.impl;

import io.jsonwebtoken.ExpiredJwtException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import ru.clevertec.authservice.mapper.AuthorityMapper;
import ru.clevertec.authservice.security.jwt.util.KeyUtil;
import ru.clevertec.authservice.util.factory.SecurityContextFactory;
import ru.clevertec.authservice.util.jwt.JwtTestData;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class JwtProviderImplTest {

    @Mock
    private AuthorityMapper authorityMapper;

    @InjectMocks
    private JwtProviderImpl jwtProvider;

    @BeforeEach
    void setUp() {
        String username = "erik_gibson";
        List<String> roles = List.of("ADMIN");
        SecurityContextFactory.buildSecurityContext(username, roles);

        Long jwtExpirationMillis = 86400000L;
        String jwtSecretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        ReflectionTestUtils.setField(jwtProvider, "jwtExpirationMillis", jwtExpirationMillis);
        ReflectionTestUtils.setField(KeyUtil.class, "jwtSecretKey", jwtSecretKey);
    }

    @Test
    void checkGenerateTokenShouldReturnToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = Collections.singletonList("ROLE_ADMIN");

        doReturn(authorities).when(authorityMapper).mapToAuthorities(authentication.getAuthorities());

        String actualToken = jwtProvider.generateToken(authentication);

        assertThat(actualToken).isNotEmpty();
    }

    @Test
    void checkIsTokenValidShouldReturnTrue() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String validJwt = JwtTestData.buildErikGibsonValidJwt().getAccessToken();

        boolean actualIsTokenValid = jwtProvider.isTokenValid(validJwt, authentication);

        assertThat(actualIsTokenValid).isTrue();
    }

    @Test
    void checkIsTokenValidShouldReturnFalse() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String validJwt = JwtTestData.buildThomasMartinezValidJwt().getAccessToken();

        boolean actualIsTokenValid = jwtProvider.isTokenValid(validJwt, authentication);

        assertThat(actualIsTokenValid).isFalse();
    }

    @Test
    void checkIsTokenValidShouldThrowsExpiredJwtException() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String expiredJwt = JwtTestData.buildErikGibsonExpiredJwt().getAccessToken();

        assertThatThrownBy(() -> jwtProvider.isTokenValid(expiredJwt, authentication))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void checkIsTokenExpiredShouldReturnFalse() throws IOException {
        String validJwt = JwtTestData.buildThomasMartinezValidJwt().getAccessToken();

        boolean actualIsTokenExpired = jwtProvider.isTokenExpired(validJwt);

        assertThat(actualIsTokenExpired).isFalse();
    }

    @Test
    void checkIsTokenExpiredShouldThrowsExpiredJwtException() throws IOException {
        String expiredJwt = JwtTestData.buildErikGibsonExpiredJwt().getAccessToken();

        assertThatThrownBy(() -> jwtProvider.isTokenExpired(expiredJwt))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
