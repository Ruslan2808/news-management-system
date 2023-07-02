package ru.clevertec.newsservice.listener;

import org.junit.jupiter.api.Test;

import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.util.entity.CommentTestBuilder;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MILLIS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CommentListenerTest {

    private final CommentListener commentListener = new CommentListener();

    @Test
    void checkBeforeSaveShouldContainsTime() {
        Comment comment = CommentTestBuilder.comment().build();

        commentListener.beforeSave(comment);

        assertThat(comment.getTime()).isCloseTo(LocalDateTime.now(), within(500, MILLIS));
    }

    @Test
    void checkBeforeUpdateShouldContainsTime() {
        Comment comment = CommentTestBuilder.comment().build();

        commentListener.beforeUpdate(comment);

        assertThat(comment.getTime()).isCloseTo(LocalDateTime.now(), within(500, MILLIS));
    }
}
