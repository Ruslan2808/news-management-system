package ru.clevertec.exceptionhandlingstarter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.clevertec.exceptionhandlingstarter.handler.GlobalExceptionHandler;

/**
 * Class for autoconfiguration of exception handling. If the property exception.handling.enabled = false
 * (by default - true) then the exception handling configuration bean will not be created
 *
 * @author Ruslan Kantsevich
 * */
@Configuration
@ConditionalOnProperty(
        prefix = "exception.handling",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ExceptionHandlingAutoConfig {

    /**
     * Creates a bean of type {@link GlobalExceptionHandler} for exception handling. If a class with this type
     * already exists in the project then this bean will not be created
     *
     * @return object of type {@link GlobalExceptionHandler} for exception handling
     * */
    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
