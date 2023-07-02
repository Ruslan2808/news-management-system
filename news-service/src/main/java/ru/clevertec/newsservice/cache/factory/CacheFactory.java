package ru.clevertec.newsservice.cache.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import ru.clevertec.exceptionhandlingstarter.exception.CacheNotDefinedException;
import ru.clevertec.newsservice.cache.Cache;
import ru.clevertec.newsservice.cache.impl.LFUCache;
import ru.clevertec.newsservice.cache.impl.LRUCache;

@Component
@ConditionalOnProperty(
        prefix = "cache",
        name = { "algorithm", "capacity" }
)
public class CacheFactory {

    @Value("${cache.algorithm}")
    private String algorithm;

    @Value("${cache.capacity}")
    private int capacity;

    public <K, V> Cache<K, V> createCache() {
        return switch (algorithm) {
            case "LRU" -> new LRUCache<>(capacity);
            case "LFU" -> new LFUCache<>(capacity);
            default -> throw new CacheNotDefinedException("Cache algorithm not defined");
        };
    }
}
