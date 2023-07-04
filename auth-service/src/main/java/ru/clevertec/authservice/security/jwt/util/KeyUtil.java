package ru.clevertec.authservice.security.jwt.util;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

/**
 * Utility class to get sign in key for JSON Web Token
 *
 * @author Ruslan Kantsevich
 * */
public class KeyUtil {

    private static String jwtSecretKey;

    /**
     * Gets sign in key for JSON Web Token
     *
     * @return object of type {@link Key} containing information about sign in key
     * */
    public static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static void setJwtSecretKey(String secretKey) {
        jwtSecretKey = secretKey;
    }
}
