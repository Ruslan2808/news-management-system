package ru.clevertec.newsservice.cache;

import java.util.Optional;

public interface Cache<K, V> {
    Optional<V> get(K key);
    Optional<V> put(K key, V value);
    Optional<V> remove(K key);
    boolean containsKey(K key);
    int size();
    void clear();
}
