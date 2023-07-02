package ru.clevertec.authservice.security.jwt.initializer;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.clevertec.authservice.security.jwt.util.KeyUtil;

@Component
public class JwtStaticContextInitializer {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @PostConstruct
    public void init() {
        KeyUtil.setJwtSecretKey(jwtSecretKey);
    }
}
