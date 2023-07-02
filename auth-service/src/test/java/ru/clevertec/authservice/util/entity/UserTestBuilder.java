package ru.clevertec.authservice.util.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.authservice.entity.Role;
import ru.clevertec.authservice.entity.User;
import ru.clevertec.authservice.util.TestBuilder;

import static ru.clevertec.authservice.entity.Role.SUBSCRIBER;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "user")
public class UserTestBuilder implements TestBuilder<User> {

    private Long id = 1L;
    private String username = "";
    private String password = "";
    private Role role = SUBSCRIBER;

    @Override
    public User build() {
        final User user = new User();

        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        return user;
    }
}
