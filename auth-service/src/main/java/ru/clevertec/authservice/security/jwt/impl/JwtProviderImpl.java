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

@Service
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private static final String JWT_TYPE_HEADER = "typ";
    private static final String AUTHORITIES_CLAIM = "authorities";

    @Value("${security.jwt.expiration-millis}")
    private Long jwtExpirationMillis;

    private final AuthorityMapper authorityMapper;

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

    @Override
    public boolean isTokenValid(String jwt, Authentication authentication) {
        String username = ClaimsUtil.extractClaim(jwt, Claims::getSubject);
        return username.equals(authentication.getName()) && !isTokenExpired(jwt);
    }

    @Override
    public boolean isTokenExpired(String jwt) {
        Date now = new Date();
        Date expiration = ClaimsUtil.extractClaim(jwt, Claims::getExpiration);

        return expiration.before(now);
    }
}
