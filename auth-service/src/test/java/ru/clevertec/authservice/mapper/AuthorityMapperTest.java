package ru.clevertec.authservice.mapper;

import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorityMapperTest {

    private final AuthorityMapper authorityMapper = Mappers.getMapper(AuthorityMapper.class);

    @Test
    void checkMapToAuthoritiesShouldReturnAuthorities() {
        List<String> roles = List.of("ADMIN", "JOURNALIST", "ROLE_SUBSCRIBER");
        Collection<? extends GrantedAuthority> grantedAuthorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        List<String> expectedAuthorities = List.of("ROLE_ADMIN", "ROLE_JOURNALIST", "ROLE_SUBSCRIBER");

        List<String> actualAuthorities = authorityMapper.mapToAuthorities(grantedAuthorities);

        assertThat(actualAuthorities).isEqualTo(expectedAuthorities);
    }

    @Test
    void checkMapToAuthoritiesShouldReturnEmptyAuthorities() {
        Collection<? extends GrantedAuthority> grantedAuthorities = Collections.emptyList();

        List<String> actualAuthorities = authorityMapper.mapToAuthorities(grantedAuthorities);

        assertThat(actualAuthorities).isEmpty();
    }

    @Test
    void checkMapToAuthoritiesShouldThrowsNullPointerException() {
        assertThatThrownBy(() -> authorityMapper.mapToAuthorities(null))
                .isInstanceOf(NullPointerException.class);
    }
}
