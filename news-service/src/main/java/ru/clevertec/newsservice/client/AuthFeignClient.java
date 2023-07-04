package ru.clevertec.newsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import ru.clevertec.newsservice.dto.response.user.UserResponse;

/**
 * Client class for sending requests to the auth-service
 *
 * @author Ruslan Kantsevich
 * */
@FeignClient("auth-service")
public interface AuthFeignClient {

    /**
     * Validates the JSON Web Token. If the token is valid then it returns information about
     * the user authorized (username and authorities) in the application
     *
     * @param authorizationHeader authorization header of HTTP request
     * @return object of type {@link UserResponse} containing information about username and authorities of user
     * */
    @GetMapping("api/v1/auth/validate")
    UserResponse validate(@RequestHeader("Authorization") String authorizationHeader);

}
