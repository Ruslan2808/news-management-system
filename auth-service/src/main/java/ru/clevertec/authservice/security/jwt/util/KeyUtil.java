package ru.clevertec.authservice.security.jwt.util;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class KeyUtil {

    private static String jwtSecretKey;

    public static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static void setJwtSecretKey(String secretKey) {
        jwtSecretKey = secretKey;
    }
}
