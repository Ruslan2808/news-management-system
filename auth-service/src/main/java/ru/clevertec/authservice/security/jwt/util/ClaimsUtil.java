package ru.clevertec.authservice.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.function.Function;

/**
 * Utility class that provides methods for working with JSON Web Token claims
 *
 * @author Ruslan Kantsevich
 * */
public class ClaimsUtil {

    /**
     * Extracts a specific claim from a JSON Web Token
     *
     * @param jwt JSON Web Token
     * @param claimsResolver claims resolver of type {@link Function}
     * @return extracted a specific claim
     * */
    public static <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JSON Web Token
     *
     * @param jwt JSON Web Token
     * @return object of type {@link Claims} containing information about all claims
     * */
    public static Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(KeyUtil.getSignInKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
