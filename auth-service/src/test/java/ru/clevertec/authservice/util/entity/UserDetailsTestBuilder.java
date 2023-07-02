package ru.clevertec.authservice.util.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import org.springframework.security.core.userdetails.UserDetails;

import ru.clevertec.authservice.entity.User;
import ru.clevertec.authservice.security.impl.UserDetailsImpl;
import ru.clevertec.authservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "userDetails")
public class UserDetailsTestBuilder implements TestBuilder<UserDetails> {

    private User user = new User();

    @Override
    public UserDetails build() {
        final UserDetailsImpl userDetails = new UserDetailsImpl();

        userDetails.setUser(user);

        return userDetails;
    }
}
