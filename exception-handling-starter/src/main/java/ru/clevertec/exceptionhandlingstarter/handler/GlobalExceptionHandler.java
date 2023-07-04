package ru.clevertec.exceptionhandlingstarter.handler;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.clevertec.exceptionhandlingstarter.dto.ErrorResponse;
import ru.clevertec.exceptionhandlingstarter.exception.CommentNotFoundException;
import ru.clevertec.exceptionhandlingstarter.exception.InvalidJwtException;
import ru.clevertec.exceptionhandlingstarter.exception.JwtNotFoundException;
import ru.clevertec.exceptionhandlingstarter.exception.NewsNotFoundException;
import ru.clevertec.exceptionhandlingstarter.exception.UserAlreadyExistsException;
import ru.clevertec.exceptionhandlingstarter.util.ErrorResponseUtil;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * Class for handling exceptions that occurred during HTTP requests
 *
 * @author Ruslan Kantsevich
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles an exception to type {@link MethodArgumentNotValidException} and returns the response with HTTP status
     * 400 (BAD_REQUEST)
     *
     * @param request   - object of type {@link HttpServletRequest} containing information about the HTTP request
     * @param exception - object of type {@link MethodArgumentNotValidException} containing information about
     *                  an exception that occurred when validating the fields of the HTTP request body
     * @return object of type {@link ResponseEntity<ErrorResponse>} containing information about the error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException exception) {
        ErrorResponse errorResponse = ErrorResponseUtil.buildValidationErrorResponse(request, exception, BAD_REQUEST);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles an exception to type {@link InvalidJwtException} and {@link JwtNotFoundException} and returns
     * the response with HTTP status 401 (UNAUTHORIZED)
     *
     * @param request   - object of type {@link HttpServletRequest} containing information about the HTTP request
     * @param exception - object of type {@link Exception} containing information about an exception that occurred
     *                  while working with a JSON Web Token
     * @return object of type {@link ResponseEntity<ErrorResponse>} containing information about the error
     */
    @ExceptionHandler({InvalidJwtException.class, JwtNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(HttpServletRequest request, Exception exception) {
        ErrorResponse errorResponse = ErrorResponseUtil.buildCommonErrorResponse(request, exception, UNAUTHORIZED);
        return ResponseEntity.status(UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handles an exception to type {@link NewsNotFoundException} and {@link CommentNotFoundException} and returns
     * the response with HTTP status 404 (NOT_FOUND)
     *
     * @param request   - object of type {@link HttpServletRequest} containing information about the HTTP request
     * @param exception - object of type {@link Exception} containing information about an exception that occurred
     *                  when a news or comment was not found
     * @return object of type {@link ResponseEntity<ErrorResponse>} containing information about the error
     */
    @ExceptionHandler({NewsNotFoundException.class, CommentNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFound(HttpServletRequest request, Exception exception) {
        ErrorResponse errorResponse = ErrorResponseUtil.buildCommonErrorResponse(request, exception, NOT_FOUND);
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles an exception to type {@link UserAlreadyExistsException} and returns the response with HTTP status
     * 409 (CONFLICT)
     *
     * @param request   - object of type {@link HttpServletRequest} containing information about the HTTP request
     * @param exception - object of type {@link UserAlreadyExistsException} containing information about
     *                  an exception that occurred when a user with submitted data already exists
     * @return object of type {@link ResponseEntity<ErrorResponse>} containing information about the error
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(HttpServletRequest request, UserAlreadyExistsException exception) {
        ErrorResponse errorResponse = ErrorResponseUtil.buildCommonErrorResponse(request, exception, CONFLICT);
        return ResponseEntity.status(CONFLICT).body(errorResponse);
    }
}
