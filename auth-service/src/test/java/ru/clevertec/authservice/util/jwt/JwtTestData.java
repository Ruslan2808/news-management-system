package ru.clevertec.authservice.util.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import ru.clevertec.authservice.dto.response.JwtResponse;

import java.io.IOException;
import java.io.InputStream;

public class JwtTestData {

    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();

    public static JwtResponse buildErikGibsonValidJwt() throws IOException {
        InputStream json = load("__files/erik_gibson_valid_jwt.json");
        return objectMapper.readValue(json, JwtResponse.class);
    }

    public static JwtResponse buildThomasMartinezValidJwt() throws IOException {
        InputStream json = load("__files/thomas_martinez_valid_jwt.json");
        return objectMapper.readValue(json, JwtResponse.class);
    }

    public static JwtResponse buildErikGibsonExpiredJwt() throws IOException {
        InputStream json = load("__files/erik_gibson_expired_jwt.json");
        return objectMapper.readValue(json, JwtResponse.class);
    }

    private static InputStream load(String fileName) {
        return ClassLoader.getSystemResourceAsStream(fileName);
    }
}
