package ru.clevertec.newsservice.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

/**
 * Class for application redis cache configuration
 *
 * @author Ruslan Kantsevich
 * */
@Configuration
@EnableCaching
@ConditionalOnProperty(
        prefix = "spring.cache",
        name = "type",
        havingValue = "redis"
)
public class RedisCacheConfig {

    @Bean
    @ConditionalOnBean(RedisCacheConfig.class)
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return redisCacheBuilder -> redisCacheBuilder
                .withCacheConfiguration("newsCache", defaultCacheConfig())
                .withCacheConfiguration("commentsCache", defaultCacheConfig());
    }
}
