package ru.clevertec.loggingstarter.aspect;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ru.clevertec.loggingstarter.annotation.Loggable;

/**
 * Class for logging HTTP requests, responses and exceptions
 *
 * @author Ruslan Kantsevich
 */
@Slf4j
@Aspect
public class LoggingAspect {

    /**
     * Pointcut to the HTTP request handler method marked with the annotation {@link Loggable}
     */
    @Pointcut("@annotation(ru.clevertec.loggingstarter.annotation.Loggable) && " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping) && " +
            "execution(* ru.clevertec.*.controller.*.*(..))")
    private void restControllerHttpMethod() {}

    /**
     * Pointcut all HTTP request handler methods in REST controller marked with the annotation {@link Loggable}
     */
    @Pointcut("@within(ru.clevertec.loggingstarter.annotation.Loggable) && " +
            "@within(org.springframework.web.bind.annotation.RequestMapping) && " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    private void restControllerHttpMethods() {}

    /**
     * Logs HTTP request
     *
     * @param joinPoint object of type {@link JoinPoint} http request handler method
     * */
    @Before("restControllerHttpMethods() || restControllerHttpMethod()")
    public void requestLoggingInHttpMethodHandler(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        String logMessage = "On http request of type = [{}] on path = [{}] was called {}.{}() method with arguments = [{}]";

        log.info(logMessage,
                request.getMethod(),
                request.getRequestURI(),
                joinPoint.getSignature().getDeclaringType().getName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    /**
     * Logs HTTP response
     *
     * @param joinPoint object of type {@link JoinPoint} http request handler method
     * @param response object of type {@link ResponseEntity} that contains information about the response
     * */
    @AfterReturning(value = "restControllerHttpMethods() || restControllerHttpMethod()", returning = "response")
    public void responseLoggingInHttpMethodHandler(JoinPoint joinPoint, ResponseEntity<?> response) {
        HttpServletRequest request = getHttpServletRequest();
        String logMessage = "On http request of type = [{}] on path = [{}] {}.{}() method returned a response with status = [{}] and the body = [{}]";

        log.info(logMessage,
                request.getMethod(),
                request.getRequestURI(),
                joinPoint.getSignature().getDeclaringType().getName(),
                joinPoint.getSignature().getName(),
                response.getStatusCode(),
                response.getBody());
    }

    /**
     * Logs an exception thrown while handling an HTTP request
     *
     * @param joinPoint object of type {@link JoinPoint} http request handler method
     * @param exception object of type {@link Exception} that contains information about the thrown exception
     * */
    @AfterThrowing(value = "restControllerHttpMethods() || restControllerHttpMethod()", throwing = "exception")
    public void exceptionLoggingInHttpMethodHandler(JoinPoint joinPoint, Exception exception) {
        HttpServletRequest request = getHttpServletRequest();
        String logMessage = "On http request of type [{}] on path [{}] {}.{}() method threw exception of type [{}] with message [{}]";

        log.error(logMessage,
                request.getMethod(),
                request.getRequestURI(),
                joinPoint.getSignature().getDeclaringType().getName(),
                joinPoint.getSignature().getName(),
                exception.getClass().getName(),
                exception.getMessage());
    }

    /**
     * Gets an object of type {@link HttpServletRequest} containing information about HTTP request
     *
     * @return object of type {@link HttpServletRequest} containing information about HTTP request
     * */
    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
