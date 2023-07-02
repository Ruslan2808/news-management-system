package ru.clevertec.authservice.security.jwt.filter;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ru.clevertec.authservice.security.impl.UserDetailsServiceImpl;
import ru.clevertec.authservice.security.jwt.impl.JwtProviderImpl;
import ru.clevertec.authservice.security.jwt.util.ClaimsUtil;
import ru.clevertec.exceptionhandlingstarter.exception.InvalidJwtException;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import static ru.clevertec.authservice.security.jwt.util.JwtUtil.BEARER_PREFIX;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProviderImpl jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authorizationHeader.substring(BEARER_PREFIX.length() + 1);
        if (jwtTokenProvider.isTokenExpired(jwtToken)) {
            throw new InvalidJwtException("Jwt expired");
        }

        String username = ClaimsUtil.extractClaim(jwtToken, Claims::getSubject);
        if (Objects.isNull(username)) {
            throw new InvalidJwtException("Jwt doesn't contain subject");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
