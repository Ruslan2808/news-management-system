package ru.clevertec.newsservice.listener;

import org.junit.jupiter.api.Test;

import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.util.entity.NewsTestBuilder;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MILLIS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class NewsListenerTest {

    private final NewsListener newsListener = new NewsListener();

    @Test
    void checkBeforeSaveShouldContainsTime() {
        News news = NewsTestBuilder.news().build();

        newsListener.beforeSave(news);

        assertThat(news.getTime()).isCloseTo(LocalDateTime.now(), within(500, MILLIS));
    }

    @Test
    void checkBeforeUpdateShouldContainsTime() {
        News news = NewsTestBuilder.news().build();

        newsListener.beforeUpdate(news);

        assertThat(news.getTime()).isCloseTo(LocalDateTime.now(), within(500, MILLIS));
    }
}
