package ru.clevertec.loggingstarter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.clevertec.loggingstarter.aspect.LoggingAspect;

/**
 * Class for autoconfiguration of logging HTTP requests, responses and exceptions. If the property
 * logging.enabled = false (by default - true) then the logging configuration bean will not be created
 *
 * @author Ruslan Kantsevich
 */
@Configuration
@ConditionalOnProperty(
        prefix = "logging",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class LoggingAutoConfig {

    /**
     * Creates a bean of type {@link LoggingAspect} for logging HTTP requests, responses and exceptions.
     * If a class with this type already exists in the project then this bean will not be created
     *
     * @return object of type {@link LoggingAspect} for logging HTTP requests, responses and exceptions
     */
    @Bean
    @ConditionalOnMissingBean(LoggingAspect.class)
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
