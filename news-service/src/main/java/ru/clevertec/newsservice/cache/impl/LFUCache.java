package ru.clevertec.newsservice.cache.impl;

import lombok.Data;

import ru.clevertec.newsservice.cache.Cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

@Data
public class LFUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, V> cache;
    private final Map<K, Integer> keyFreq;
    private final Map<Integer, LinkedHashSet<K>> orderKeyFreq;
    private int minKeyFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.keyFreq = new HashMap<>();
        this.orderKeyFreq = new HashMap<>();
        this.minKeyFreq = 0;
    }

    @Override
    public Optional<V> get(K key) {
        if (!cache.containsKey(key)) {
            return Optional.empty();
        }

        updateFreq(key);
        V value = cache.get(key);

        return Optional.ofNullable(value);
    }

    @Override
    public Optional<V> put(K key, V value) {
        if (cache.containsKey(key)) {
            updateFreq(key);

            cache.put(key, value);
            V putValue = cache.get(key);

            return Optional.ofNullable(putValue);
        } else if (cache.size() == capacity) {
            LinkedHashSet<K> setFreq = orderKeyFreq.get(minKeyFreq);
            K leastUsedKey = setFreq.iterator().next();

            setFreq.remove(leastUsedKey);
            keyFreq.remove(leastUsedKey);
            cache.remove(leastUsedKey);
        }

        keyFreq.put(key, 1);
        orderKeyFreq.putIfAbsent(1, new LinkedHashSet<>());
        orderKeyFreq.get(1).add(key);
        minKeyFreq = 1;

        cache.put(key, value);
        V putValue = cache.get(key);

        return Optional.ofNullable(putValue);
    }

    @Override
    public Optional<V> remove(K key) {
        if (!cache.containsKey(key)) {
            return Optional.empty();
        }

        int freq = keyFreq.remove(key);
        orderKeyFreq.get(freq).remove(key);
        V removeValue = cache.remove(key);

        if (freq == minKeyFreq && orderKeyFreq.get(freq).isEmpty()) {
            minKeyFreq++;
        }

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
        keyFreq.clear();
        orderKeyFreq.clear();
    }

    private void updateFreq(K key) {
        int freq = keyFreq.get(key);
        keyFreq.put(key, freq + 1);

        orderKeyFreq.get(freq).remove(key);
        orderKeyFreq.putIfAbsent(freq + 1, new LinkedHashSet<>());
        orderKeyFreq.get(freq + 1).add(key);

        if (freq == minKeyFreq && orderKeyFreq.get(freq).isEmpty()) {
            minKeyFreq++;
        }
    }
}
