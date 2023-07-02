package ru.clevertec.authservice.util.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.authservice.dto.request.SignUpRequest;
import ru.clevertec.authservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "signUpRequest")
public class SignUpRequestTestBuilder implements TestBuilder<SignUpRequest> {

    private String username = "";
    private String password = "";
    private String role = "SUBSCRIBER";

    @Override
    public SignUpRequest build() {
        final SignUpRequest signUpRequest = new SignUpRequest();

        signUpRequest.setUsername(username);
        signUpRequest.setPassword(password);
        signUpRequest.setRole(role);

        return signUpRequest;
    }
}
