package ru.clevertec.authservice.integration.repository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import ru.clevertec.authservice.entity.User;
import ru.clevertec.authservice.integration.BaseIntegrationTest;
import ru.clevertec.authservice.repository.UserRepository;
import ru.clevertec.authservice.util.entity.UserTestBuilder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertAll;

import static ru.clevertec.authservice.entity.Role.ADMIN;

public class UserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void checkFindByUsernameShouldReturnUser() {
        String username = "erik_gibson";
        String password = "$2y$10$7d4u75rP/8X/JuXMB2oGkuAn6uV30IGMj8jwV5HSDdGfo09g5w9Qu";
        User expectedUser = UserTestBuilder.user()
                .withUsername(username)
                .withPassword(password)
                .withRole(ADMIN)
                .build();

        Optional<User> actualUser = userRepository.findByUsername(username);

        assertThat(actualUser).isPresent();
        assertAll(() -> {
            assertThat(actualUser.get().getUsername()).isEqualTo(expectedUser.getUsername());
            assertThat(actualUser.get().getPassword()).isEqualTo(expectedUser.getPassword());
            assertThat(actualUser.get().getRole()).isEqualTo(expectedUser.getRole());
        });
    }

    @Test
    void checkFindByUsernameShouldReturnEmptyUser() {
        String username = "ivan_ivanov";

        Optional<User> actualUser = userRepository.findByUsername(username);

        assertThat(actualUser).isNotPresent();
    }
}
