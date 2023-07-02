package ru.clevertec.authservice.util.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.authservice.dto.request.LogInRequest;
import ru.clevertec.authservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "logInRequest")
public class LogInRequestTestBuilder implements TestBuilder<LogInRequest> {

    private String username = "";
    private String password = "";

    @Override
    public LogInRequest build() {
        final LogInRequest logInRequest = new LogInRequest();

        logInRequest.setUsername(username);
        logInRequest.setPassword(password);

        return logInRequest;
    }
}
