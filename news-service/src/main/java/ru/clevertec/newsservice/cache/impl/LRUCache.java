package ru.clevertec.newsservice.cache.impl;

import lombok.Data;

import ru.clevertec.newsservice.cache.Cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of the {@link Cache} interface that uses the least recently used (LRU) algorithm,
 * where the key-value mappings that has not been used the longest is evicted from the cache.
 * {@link LinkedHashMap} is used to store key-value mappings, including their order.
 * The least recently used item is stored at the beginning of the cache.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 * @author Ruslan Kantsevich
 */
@Data
public class LRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, V> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity);
    }

    /**
     * Returns key mapping as {@link Optional} from this cache, if present
     *
     * @param key the key whose mapping is to be retrieved from the cache
     * @return the {@link Optional} value associated with key, or {@code Optional.empty()}
     * if there was no mapping for key
     */
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

    /**
     * Associates the specified value with the specified key in this cache. If the cache
     * previously contained a mapping for the key, the old value is replaced with the
     * specified value
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the {@link Optional} value associated with key, or {@code Optional.empty()}
     * if there was no mapping for key
     */
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

    /**
     * Removes the mapping for a key from this cache if it is present
     *
     * @param key key whose mapping is to be removed from the cache
     * @return the {@link Optional} value associated with key, or {@code Optional.empty()}
     * if there was no mapping for key
     */
    @Override
    public Optional<V> remove(K key) {
        if (!cache.containsKey(key)) {
            return Optional.empty();
        }

        V removeValue = cache.remove(key);

        return Optional.ofNullable(removeValue);
    }

    /**
     * Checks if the value with the key is in the cache. If contained then returns true.
     * Otherwise - false
     *
     * @param key whose mapping is to be removed from the cache
     * @return variable containing information about the contained value by key in the cache
     * */
    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    /**
     * Returns the number of key-value mappings in this cache
     *
     * @return the number of key-value mappings in this cache
     */
    @Override
    public int size() {
        return cache.size();
    }

    /**
     * Removes all key-value mappings from this cache. The cache will be empty after this
     * call returns
     */
    @Override
    public void clear() {
        cache.clear();
    }
}
