package ru.clevertec.newsservice.security.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import ru.clevertec.exceptionhandlingstarter.dto.ErrorResponse;
import ru.clevertec.exceptionhandlingstarter.util.ErrorResponseUtil;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
@RequiredArgsConstructor
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        ErrorResponse errorResponse = ErrorResponseUtil
                .buildCommonErrorResponse(request, exception, FORBIDDEN);
        String jsonErrorResponse = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(jsonErrorResponse);
    }
}
