package ru.clevertec.newsservice.cache;

import java.util.Optional;

/**
 * Interface for performing operations with the cache, which acts as a temporary storage
 * of frequently used objects
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 * @author Ruslan Kantsevich
 */
public interface Cache<K, V> {

    Optional<V> get(K key);
    Optional<V> put(K key, V value);
    Optional<V> remove(K key);
    boolean containsKey(K key);
    int size();
    void clear();

}
