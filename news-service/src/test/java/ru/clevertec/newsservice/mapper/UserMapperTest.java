package ru.clevertec.newsservice.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.mapstruct.factory.Mappers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import ru.clevertec.newsservice.dto.response.user.UserResponse;
import ru.clevertec.newsservice.util.response.UserResponseTestBuilder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void checkMapToUserDetailsShouldReturnUserDetails() {
        String username = "thomas_martinez";
        List<String> authorities = List.of("ROLE_JOURNALIST");
        Collection<? extends GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        UserResponse userResponse = UserResponseTestBuilder.userResponse()
                .withUsername(username)
                .withAuthorities(authorities)
                .build();
        UserDetails expectedUserDetails = User.builder()
                .username(username)
                .password("")
                .authorities(grantedAuthorities)
                .build();

        UserDetails actualUserDetails = userMapper.mapToUserDetails(userResponse);

        assertThat(actualUserDetails).isEqualTo(expectedUserDetails);
    }

    @Test
    void checkMapToUserDetailsShouldThrowsIllegalArgumentException() {
        UserResponse userResponse = UserResponseTestBuilder.userResponse()
                .withUsername(null)
                .withAuthorities(List.of("ROLE_JOURNALIST"))
                .build();

        assertThatThrownBy(() -> userMapper.mapToUserDetails(userResponse))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("provideNullUserResponseParams")
    void checkMapToUserDetailsShouldThrowsNullPointerException(UserResponse userResponse) {
        assertThatThrownBy(() -> userMapper.mapToUserDetails(userResponse))
                .isInstanceOf(NullPointerException.class);
    }

    private static Stream<UserResponse> provideNullUserResponseParams() {
        return Stream.of(
                null,
                UserResponseTestBuilder.userResponse()
                        .withUsername("thomas_martinez")
                        .withAuthorities(null)
                        .build()
        );
    }
}
