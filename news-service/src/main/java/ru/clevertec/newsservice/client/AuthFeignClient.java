package ru.clevertec.newsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import ru.clevertec.newsservice.dto.response.user.UserResponse;

@FeignClient("auth-service")
public interface AuthFeignClient {

    @GetMapping("api/v1/auth/validate")
    UserResponse validate(@RequestHeader("Authorization") String authorizationHeader);
}
