package ru.clevertec.authservice.security.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.clevertec.authservice.entity.User;
import ru.clevertec.authservice.repository.UserRepository;
import ru.clevertec.authservice.util.entity.UserDetailsTestBuilder;
import ru.clevertec.authservice.util.entity.UserTestBuilder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void checkLoadByUsernameShouldReturnUserDetails() {
        String username = "erik_gibson";
        String password = "$2y$10$7d4u75rP/8X/JuXMB2oGkuAn6uV30IGMj8jwV5HSDdGfo09g5w9Qu";
        User user = UserTestBuilder.user()
                .withUsername(username)
                .withPassword(password)
                .build();
        UserDetails expectedUserDetails = UserDetailsTestBuilder.userDetails()
                .withUser(user)
                .build();

        doReturn(Optional.of(user)).when(userRepository).findByUsername(username);

        UserDetails actualUserDetails = userDetailsService.loadUserByUsername(username);

        assertThat(actualUserDetails).isEqualTo(expectedUserDetails);
    }

    @Test
    void checkLoadByUsernameShouldThrowsUsernameNotFoundException() {
        String username = "ivan_ivanov";

        doReturn(Optional.empty()).when(userRepository).findByUsername(username);

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
