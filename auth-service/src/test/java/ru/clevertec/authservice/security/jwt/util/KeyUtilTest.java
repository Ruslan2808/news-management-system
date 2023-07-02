package ru.clevertec.authservice.security.jwt.util;

import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;

import static org.assertj.core.api.Assertions.assertThat;

class KeyUtilTest {

    @Test
    void checkGetSignInKeyShouldReturnSignInKey() {
        String jwtSecretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        ReflectionTestUtils.setField(KeyUtil.class, "jwtSecretKey", jwtSecretKey);

        Key actualSignInKey = KeyUtil.getSignInKey();

        assertThat(actualSignInKey).isNotNull();
    }
}
