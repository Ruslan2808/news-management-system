package ru.clevertec.newsservice.util.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.response.user.UserResponse;
import ru.clevertec.newsservice.util.TestBuilder;

import java.util.ArrayList;
import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "userResponse")
public class UserResponseTestBuilder implements TestBuilder<UserResponse> {

    private String username = "";
    private List<String> authorities = new ArrayList<>();

    @Override
    public UserResponse build() {
        final UserResponse userResponse = new UserResponse();

        userResponse.setUsername(username);
        userResponse.setAuthorities(authorities);

        return userResponse;
    }
}
