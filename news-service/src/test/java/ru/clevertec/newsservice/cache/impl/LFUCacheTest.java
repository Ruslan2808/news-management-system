package ru.clevertec.newsservice.cache.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertAll;

class LFUCacheTest {

    private LFUCache<Long, String> lfuCache;

    @BeforeEach
    void setUp() {
        lfuCache = new LFUCache<>(3);
    }

    @Test
    void checkGetShouldReturnString() {
        Long key = 1L;
        String expectedString = "One";

        lfuCache.put(key, expectedString);

        Optional<String> actualString = lfuCache.get(key);

        assertThat(actualString).isPresent();
        assertThat(actualString.get()).isEqualTo(expectedString);
    }

    @Test
    void checkGetShouldReturnNullString() {
        lfuCache.put(1L, "One");
        lfuCache.put(3L, "Three");
        lfuCache.put(2L, "Two");
        lfuCache.put(4L, "Four");

        Optional<String> actualString = lfuCache.get(1L);

        assertThat(actualString).isNotPresent();
    }

    @Test
    void checkPutShouldReturnString() {
        String expectedString = "One";

        Optional<String> actualString = lfuCache.put(1L, expectedString);

        assertThat(actualString).isPresent();
        assertThat(actualString.get()).isEqualTo(expectedString);
    }

    @Test
    void checkPutShouldReturnCache() {
        lfuCache.put(1L, "One");
        lfuCache.put(3L, "Three");
        lfuCache.put(2L, "Two");
        lfuCache.get(3L);
        lfuCache.put(4L, "Four");
        lfuCache.put(5L, "Five");
        lfuCache.get(4L);
        lfuCache.remove(4L);
        lfuCache.put(6L, "Six");

        assertAll(
                () -> assertThat(lfuCache.getCache().keySet()).contains(3L, 5L, 6L),
                () -> assertThat(lfuCache.getCache().values()).contains("Three", "Five", "Six")
        );
    }

    @Test
    void checkPutShouldReturnOrderedKeyFreqCache() {
        lfuCache.put(1L, "One");
        lfuCache.put(3L, "Three");
        lfuCache.put(2L, "Two");
        lfuCache.get(3L);
        lfuCache.put(4L, "Four");
        lfuCache.put(5L, "Five");
        lfuCache.get(4L);

        assertAll(
                () -> assertThat(lfuCache.getOrderKeyFreq().keySet()).containsExactly(1, 2),
                () -> assertThat(lfuCache.getOrderKeyFreq().get(1)).contains(5L),
                () -> assertThat(lfuCache.getOrderKeyFreq().get(2)).contains(3L, 4L)
        );
    }

    @Test
    void checkRemoveShouldReturnString() {
        Long key = 1L;
        String expectedString = "One";

        lfuCache.put(key, expectedString);

        Optional<String> actualString = lfuCache.remove(key);

        assertThat(actualString).isPresent();
        assertThat(actualString.get()).isEqualTo(expectedString);
    }

    @Test
    void checkRemoveShouldReturnNullString() {
        lfuCache.put(1L, "One");

        Optional<String> actualString = lfuCache.remove(2L);

        assertThat(actualString).isNotPresent();
    }

    @Test
    void checkRemoveShouldReturnMinKeyFreq2() {
        int expectedMinKeyFreq = 2;

        lfuCache.put(1L, "One");
        lfuCache.put(3L, "Three");
        lfuCache.put(2L, "Two");
        lfuCache.get(3L);
        lfuCache.put(4L, "Four");
        lfuCache.put(5L, "Five");
        lfuCache.get(4L);

        lfuCache.remove(5L);
        int actualMinKeyFreq = lfuCache.getMinKeyFreq();

        assertThat(actualMinKeyFreq).isEqualTo(expectedMinKeyFreq);
    }

    @Test
    void checkContainsKeyShouldReturnTrue() {
        Long key = 1L;
        String expectedString = "One";

        lfuCache.put(key, expectedString);

        boolean actualContainsKey = lfuCache.containsKey(key);

        assertThat(actualContainsKey).isTrue();
    }

    @Test
    void checkContainsKeyShouldReturnFalse() {
        Long key = 1L;

        boolean actualContainsKey = lfuCache.containsKey(key);

        assertThat(actualContainsKey).isFalse();
    }

    @Test
    void checkSizeShouldReturn2() {
        int expectedSize = 2;

        lfuCache.put(1L, "One");
        lfuCache.put(2L, "Two");

        int actualSize = lfuCache.size();

        assertThat(actualSize).isEqualTo(expectedSize);
    }

    @Test
    void checkSizeShouldReturnSize3() {
        int expectedSize = 3;

        lfuCache.put(1L, "One");
        lfuCache.put(2L, "Two");
        lfuCache.put(3L, "Three");
        lfuCache.put(4L, "Four");

        int actualSize = lfuCache.size();

        assertThat(actualSize).isEqualTo(expectedSize);
    }

    @Test
    void checkClearShouldReturnSize0() {
        lfuCache.put(1L, "One");
        lfuCache.put(2L, "Two");

        lfuCache.clear();
        int actualSize = lfuCache.size();

        assertThat(actualSize).isZero();
    }
}
