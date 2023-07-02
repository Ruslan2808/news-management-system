package ru.clevertec.newsservice.cache.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertAll;

class LRUCacheTest {

    private LRUCache<Long, String> lruCache;

    @BeforeEach
    void setUp() {
        lruCache = new LRUCache<>(3);
    }

    @Test
    void checkGetShouldReturnString() {
        Long key = 1L;
        String expectedString = "One";

        lruCache.put(key, expectedString);

        Optional<String> actualString = lruCache.get(key);

        assertThat(actualString).isPresent();
        assertThat(actualString.get()).isEqualTo(expectedString);
    }

    @Test
    void checkGetShouldReturnNullString() {
        lruCache.put(1L, "One");
        lruCache.put(3L, "Three");
        lruCache.put(2L, "Two");
        lruCache.put(4L, "Four");

        Optional<String> actualString = lruCache.get(1L);

        assertThat(actualString).isNotPresent();
    }

    @Test
    void checkPutShouldReturnString() {
        String expectedString = "One";

        Optional<String> actualString = lruCache.put(1L, expectedString);

        assertThat(actualString).isPresent();
        assertThat(actualString.get()).isEqualTo(expectedString);
    }

    @Test
    void checkPutShouldReturnOrderedCache() {
        lruCache.put(1L, "One");
        lruCache.put(3L, "Three");
        lruCache.put(2L, "Two");
        lruCache.get(3L);
        lruCache.put(4L, "Four");
        lruCache.put(5L, "Five");
        lruCache.get(4L);

        assertAll(
                () -> assertThat(lruCache.getCache().keySet()).containsExactly(3L, 5L, 4L),
                () -> assertThat(lruCache.getCache().values()).containsExactly("Three", "Five", "Four")
        );
    }

    @Test
    void checkRemoveShouldReturnString() {
        Long key = 1L;
        String expectedString = "One";

        lruCache.put(key, expectedString);

        Optional<String> actualString = lruCache.remove(key);

        assertThat(actualString).isPresent();
        assertThat(actualString.get()).isEqualTo(expectedString);
    }

    @Test
    void checkRemoveShouldReturnNullString() {
        lruCache.put(1L, "One");

        Optional<String> actualString = lruCache.remove(2L);

        assertThat(actualString).isNotPresent();
    }

    @Test
    void checkContainsKeyShouldReturnTrue() {
        Long key = 1L;
        String expectedString = "One";

        lruCache.put(key, expectedString);

        boolean actualContainsKey = lruCache.containsKey(key);

        assertThat(actualContainsKey).isTrue();
    }

    @Test
    void checkContainsKeyShouldReturnFalse() {
        Long key = 1L;

        boolean actualContainsKey = lruCache.containsKey(key);

        assertThat(actualContainsKey).isFalse();
    }

    @Test
    void checkSizeShouldReturn2() {
        int expectedSize = 2;

        lruCache.put(1L, "One");
        lruCache.put(2L, "Two");

        int actualSize = lruCache.size();

        assertThat(actualSize).isEqualTo(expectedSize);
    }

    @Test
    void checkSizeShouldReturnSize3() {
        int expectedSize = 3;

        lruCache.put(1L, "One");
        lruCache.put(2L, "Two");
        lruCache.put(3L, "Three");
        lruCache.put(4L, "Four");

        int actualSize = lruCache.size();

        assertThat(actualSize).isEqualTo(expectedSize);
    }

    @Test
    void checkClearShouldReturnSize0() {
        lruCache.put(1L, "One");
        lruCache.put(2L, "Two");

        lruCache.clear();
        int actualSize = lruCache.size();

        assertThat(actualSize).isZero();
    }
}
