package ru.clevertec.newsservice.cache.impl;

import lombok.Data;

import ru.clevertec.newsservice.cache.Cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class LRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, V> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity);
    }

    @Override
    public Optional<V> get(K key) {
        if (!cache.containsKey(key)) {
            return Optional.empty();
        }

        V removeValue = cache.remove(key);
        cache.put(key, removeValue);
        V value = cache.get(key);

        return Optional.ofNullable(value);
    }

    @Override
    public Optional<V> put(K key, V value) {
        if (cache.containsKey(key)) {
            cache.remove(key);
        } else if (cache.size() == capacity) {
            K firstKey = cache.keySet()
                    .iterator()
                    .next();

            cache.remove(firstKey);
        }

        cache.put(key, value);
        V putValue = cache.get(key);

        return Optional.ofNullable(putValue);
    }

    @Override
    public Optional<V> remove(K key) {
        if (!cache.containsKey(key)) {
            return Optional.empty();
        }

        V removeValue = cache.remove(key);

        return Optional.ofNullable(removeValue);
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
