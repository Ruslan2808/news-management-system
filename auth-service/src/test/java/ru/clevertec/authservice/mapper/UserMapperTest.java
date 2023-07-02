package ru.clevertec.authservice.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.mapstruct.factory.Mappers;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import ru.clevertec.authservice.dto.request.SignUpRequest;
import ru.clevertec.authservice.entity.User;
import ru.clevertec.authservice.util.request.SignUpRequestTestBuilder;
import ru.clevertec.authservice.util.entity.UserTestBuilder;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.junit.jupiter.api.Assertions.assertAll;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void checkMapToUserShouldReturnUser() {
        String username = "erik_gibson";
        String password = "password";
        String encodedPassword = "$2y$10$7d4u75rP/8X/JuXMB2oGkuAn6uV30IGMj8jwV5HSDdGfo09g5w9Qu";
        SignUpRequest signUpRequest = SignUpRequestTestBuilder.signUpRequest()
                .withUsername(username)
                .withPassword(password)
                .build();
        User expectedUser = UserTestBuilder.user()
                .withUsername(username)
                .withPassword(encodedPassword)
                .build();

        doReturn(encodedPassword).when(passwordEncoder).encode(password);

        User actualUser = userMapper.mapToUser(signUpRequest);

        assertAll(() -> {
            assertThat(actualUser.getUsername()).isEqualTo(expectedUser.getUsername());
            assertThat(actualUser.getPassword()).isEqualTo(expectedUser.getPassword());
            assertThat(actualUser.getRole()).isEqualTo(expectedUser.getRole());
        });
    }

    @Test
    void checkMapToUserShouldReturnNullUserFields() {
        SignUpRequest signUpRequest = SignUpRequestTestBuilder.signUpRequest()
                .withUsername(null)
                .withPassword(null)
                .build();

        User actualUser = userMapper.mapToUser(signUpRequest);

        assertAll(() -> {
            assertThat(actualUser.getUsername()).isNull();
            assertThat(actualUser.getPassword()).isNull();
        });
    }

    @ParameterizedTest
    @MethodSource("provideNullSignUpRequestParams")
    void checkMapToUserDetailsShouldThrowsNullPointerException(SignUpRequest SignUpRequest) {
        assertThatThrownBy(() -> userMapper.mapToUser(SignUpRequest))
                .isInstanceOf(NullPointerException.class);
    }

    private static Stream<SignUpRequest> provideNullSignUpRequestParams() {
        return Stream.of(
                null,
                SignUpRequestTestBuilder.signUpRequest()
                        .withRole(null)
                        .build()
        );
    }
}
