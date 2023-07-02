package ru.clevertec.authservice.security.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import ru.clevertec.exceptionhandlingstarter.dto.ErrorResponse;
import ru.clevertec.exceptionhandlingstarter.util.ErrorResponseUtil;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        ErrorResponse errorResponse = ErrorResponseUtil
                .buildCommonErrorResponse(request, exception, UNAUTHORIZED);
        String jsonErrorResponse = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(jsonErrorResponse);
    }
}
