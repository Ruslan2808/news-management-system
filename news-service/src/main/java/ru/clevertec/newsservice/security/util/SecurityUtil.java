package ru.clevertec.newsservice.security.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class that provides methods for checking access username and role of user
 *
 * @author Ruslan Kantsevich
 * */
public class SecurityUtil {

    /**
     * Checks the username for accessibility. The username and the username of the authenticated user match
     * then true is returned. Otherwise - false
     *
     * @param username username to check
     * @return variable containing information about the accessibility of username
     * */
    public static boolean isAccessUsername(String username) {
        String authorizedUsername = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return username.equals(authorizedUsername);
    }

    /**
     * Checks the role for accessibility. The role contains in list roles of the authenticated user
     * then true is returned. Otherwise - false
     *
     * @param role role to check
     * @return variable containing information about the accessibility of user role
     * */
    public static boolean isAccessRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.contains(role));
    }
}
