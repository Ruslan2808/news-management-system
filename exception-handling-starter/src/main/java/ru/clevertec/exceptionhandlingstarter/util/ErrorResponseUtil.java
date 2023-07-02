package ru.clevertec.exceptionhandlingstarter.util;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import ru.clevertec.exceptionhandlingstarter.dto.CommonErrorResponse;
import ru.clevertec.exceptionhandlingstarter.dto.ErrorResponse;
import ru.clevertec.exceptionhandlingstarter.dto.ValidationErrorResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Utility class that provides methods to build objects of type {@link ErrorResponse},
 * which contains information about the error
 *
 * @author Ruslan Kantsevich
 */
public class ErrorResponseUtil {

    /**
     * Builds an object of type {@link CommonErrorResponse}, which is a subclass of {@link ErrorResponse}
     * and contains information about a common error
     *
     * @param request   - object of type {@link HttpServletRequest} containing information about the HTTP request
     * @param exception - object of type {@link Exception} containing information about the thrown exception
     * @param status    - object of type {@link HttpStatus} containing information about the status of the HTTP request
     * @return object of type {@link ErrorResponse}  containing information about the error
     */
    public static ErrorResponse buildCommonErrorResponse(HttpServletRequest request,
                                                         Exception exception,
                                                         HttpStatus status) {
        return CommonErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status)
                .path(request.getRequestURL().toString())
                .message(exception.getMessage())
                .build();
    }

    /**
     * Builds an object of type {@link ValidationErrorResponse}, which is a subclass of {@link ErrorResponse}
     * and contains information about a validation error of the HTTP request body
     *
     * @param request   - object of type {@link HttpServletRequest} containing information about the HTTP request
     * @param exception - object of type {@link MethodArgumentNotValidException} containing information about
     *                  an exception that occurred when validating the fields of the HTTP request body
     * @param status    - object of type {@link HttpStatus} containing information about the status of the HTTP request
     * @return object of type {@link ErrorResponse}  containing information about the error
     */
    public static ErrorResponse buildValidationErrorResponse(HttpServletRequest request,
                                                             MethodArgumentNotValidException exception,
                                                             HttpStatus status) {
        List<String> errorMessages = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status)
                .path(request.getRequestURL().toString())
                .messages(errorMessages)
                .build();
    }
}
