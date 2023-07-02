package ru.clevertec.authservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ru.clevertec.authservice.dto.request.SignUpRequest;
import ru.clevertec.authservice.entity.Role;
import ru.clevertec.authservice.entity.User;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    public User mapToUser(SignUpRequest signUpRequest) {
        String username = signUpRequest.getUsername();
        String password = passwordEncoder.encode(signUpRequest.getPassword());
        Role role = Role.valueOf(signUpRequest.getRole());

        return User.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
    }
}
