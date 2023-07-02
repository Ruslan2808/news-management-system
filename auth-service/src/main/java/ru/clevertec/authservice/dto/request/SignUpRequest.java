package ru.clevertec.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "User username cannot be empty")
    private String username;

    @NotBlank(message = "User password cannot be empty")
    private String password;

    @NotNull(message = "User role cannot be empty")
    @Pattern(regexp = "^ADMIN|JOURNALIST|SUBSCRIBER$",
             message = "User role can only be ADMIN, JOURNALIST OR SUBSCRIBER")
    private String role;

}
