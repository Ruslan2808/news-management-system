package ru.clevertec.newsservice.security.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.clevertec.newsservice.util.factory.SecurityContextFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUtilTest {

    @BeforeEach
    void setUp() {
        String username = "thomas_martinez";
        List<String> authorities = List.of("SUBSCRIBER");
        SecurityContextFactory.createSecurityContext(username, authorities);
    }

    @Test
    void checkIsAccessUsernameShouldReturnTrue() {
        String username = "thomas_martinez";

        boolean actualAccessUsername = SecurityUtil.isAccessUsername(username);

        assertThat(actualAccessUsername).isTrue();
    }

    @Test
    void checkIsAccessUsernameShouldReturnFalse() {
        String username = "sharon_hill";

        boolean actualAccessUsername = SecurityUtil.isAccessUsername(username);

        assertThat(actualAccessUsername).isFalse();
    }

    @Test
    void checkIsAccessRoleShouldReturnTrue() {
        String role = "SUBSCRIBER";

        boolean actualIsAccessRole = SecurityUtil.isAccessRole(role);

        assertThat(actualIsAccessRole).isTrue();
    }

    @Test
    void checkIsAccessRoleShouldReturnFalse() {
        String role = "JOURNALIST";

        boolean actualIsAccessRole = SecurityUtil.isAccessRole(role);

        assertThat(actualIsAccessRole).isFalse();
    }
}
