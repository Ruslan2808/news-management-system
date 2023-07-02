package ru.clevertec.authservice.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.function.Function;

public class ClaimsUtil {

    public static <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    public static Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(KeyUtil.getSignInKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
