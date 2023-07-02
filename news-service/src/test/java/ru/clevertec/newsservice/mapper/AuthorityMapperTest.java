package ru.clevertec.newsservice.mapper;

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
    void checkMapToGrantedAuthoritiesShouldReturnGrantedAuthorities() {
        List<String> authorities = List.of("ROLE_ADMIN", "ROLE_JOURNALIST", "ROLE_SUBSCRIBER");
        Collection<? extends GrantedAuthority> expectedGrantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        Collection<? extends GrantedAuthority> actualGrantedAuthorities = authorityMapper.mapToGrantedAuthorities(authorities);

        assertThat(actualGrantedAuthorities).isEqualTo(expectedGrantedAuthorities);
    }

    @Test
    void checkMapToGrantedAuthoritiesShouldReturnEmptyGrantedAuthorities() {
        List<String> authorities = Collections.emptyList();

        Collection<? extends GrantedAuthority> actualGrantedAuthorities = authorityMapper.mapToGrantedAuthorities(authorities);

        assertThat(actualGrantedAuthorities).isEmpty();
    }

    @Test
    void checkMapToGrantedAuthoritiesShouldThrowsNullPointerException() {
        assertThatThrownBy(() -> authorityMapper.mapToGrantedAuthorities(null))
                .isInstanceOf(NullPointerException.class);
    }
}
