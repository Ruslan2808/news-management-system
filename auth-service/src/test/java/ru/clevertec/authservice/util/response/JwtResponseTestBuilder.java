package ru.clevertec.authservice.util.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.authservice.dto.response.JwtResponse;
import ru.clevertec.authservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "jwtResponse")
public class JwtResponseTestBuilder implements TestBuilder<JwtResponse> {

    private String accessToken = "";

    @Override
    public JwtResponse build() {
        final JwtResponse jwtResponse = new JwtResponse();

        jwtResponse.setAccessToken(accessToken);

        return jwtResponse;
    }
}
