package ru.clevertec.newsservice.security.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static boolean isAccessUsername(String username) {
        String authorizedUsername = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return username.equals(authorizedUsername);
    }

    public static boolean isAccessRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.contains(role));
    }
}
