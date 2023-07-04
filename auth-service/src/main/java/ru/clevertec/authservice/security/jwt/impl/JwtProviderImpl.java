package ru.clevertec.authservice.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import ru.clevertec.authservice.mapper.AuthorityMapper;
import ru.clevertec.authservice.security.jwt.JwtProvider;
import ru.clevertec.authservice.security.jwt.util.ClaimsUtil;
import ru.clevertec.authservice.security.jwt.util.KeyUtil;

import java.util.Date;
import java.util.List;

/**
 * Class implementing {@link JwtProvider} that provides methods for working with JSON Web Token
 *
 * @author Ruslan Kantsevich
 * */
@Service
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private static final String JWT_TYPE_HEADER = "typ";
    private static final String AUTHORITIES_CLAIM = "authorities";

    @Value("${security.jwt.expiration-millis}")
    private Long jwtExpirationMillis;

    private final AuthorityMapper authorityMapper;

    /**
     * Generates JSON Web Token
     *
     * @param authentication object of type {@link Authentication} containing information about the authenticated user
     * @return JSON Web token
     * */
    @Override
    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpirationMillis);

        List<String> authorities = authorityMapper.mapToAuthorities(authentication.getAuthorities());

        return Jwts.builder()
                .setHeaderParam(JWT_TYPE_HEADER, Header.JWT_TYPE)
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_CLAIM, authorities)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(KeyUtil.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Checks the JSON Web token for validity. The subject name of the JSON Web token and
     * the username of the authenticated user match and the JSON Web token has not expired
     * then true is returned. Otherwise - false
     *
     * @param jwt JSON Web Token
     * @param authentication object of type {@link Authentication} containing information about the authenticated user
     * @return variable containing information about the validity of JSON Web Token
     * */
    @Override
    public boolean isTokenValid(String jwt, Authentication authentication) {
        String username = ClaimsUtil.extractClaim(jwt, Claims::getSubject);
        return username.equals(authentication.getName()) && !isTokenExpired(jwt);
    }

    /**
     * Checks the JSON Web token for expiration. The JSON Web token has not expired
     * then true is returned. Otherwise - false
     *
     * @param jwt JSON Web Token
     * @return variable containing information about the expiration of JSON Web Token
     * */
    @Override
    public boolean isTokenExpired(String jwt) {
        Date now = new Date();
        Date expiration = ClaimsUtil.extractClaim(jwt, Claims::getExpiration);

        return expiration.before(now);
    }
}
