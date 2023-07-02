package ru.clevertec.newsservice.mapper;

import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import ru.clevertec.newsservice.dto.filter.CommentFilter;
import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.util.filter.CommentFilterTestBuilder;
import ru.clevertec.newsservice.util.request.CommentRequestTestBuilder;
import ru.clevertec.newsservice.util.response.CommentResponseTestBuilder;
import ru.clevertec.newsservice.util.entity.CommentTestBuilder;
import ru.clevertec.newsservice.util.request.NewsCommentRequestTestBuilder;
import ru.clevertec.newsservice.util.response.NewsCommentResponseTestBuilder;
import ru.clevertec.newsservice.util.response.NewsResponseTestBuilder;
import ru.clevertec.newsservice.util.entity.NewsTestBuilder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertAll;

class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void checkMapToCommentShouldReturnCommentFieldsFromCommentFilter() {
        String text = "good comment";
        String username = "debbie_garcia";
        CommentFilter commentFilter = CommentFilterTestBuilder.commentFilter()
                .withText(text)
                .withUsername(username)
                .build();
        Comment expectedComment = CommentTestBuilder.comment()
                .withText(text)
                .withUsername(username)
                .build();

        Comment actualComment = commentMapper.mapToComment(commentFilter);

        assertAll(() -> {
            assertThat(actualComment.getText()).isEqualTo(expectedComment.getText());
            assertThat(actualComment.getUsername()).isEqualTo(expectedComment.getUsername());
        });
    }

    @Test
    void checkMapToCommentShouldReturnNullComment() {
        Comment actualComment = commentMapper.mapToComment(null);

        assertThat(actualComment).isNull();
    }

    @Test
    void checkMapToCommentShouldReturnNullCommentFieldsFromCommentFilter() {
        CommentFilter commentFilter = CommentFilterTestBuilder.commentFilter()
                .withUsername(null)
                .withText(null)
                .build();

        Comment actualComment = commentMapper.mapToComment(commentFilter);

        assertAll(() -> {
            assertThat(actualComment.getText()).isNull();
            assertThat(actualComment.getUsername()).isNull();
        });
    }

    @Test
    void checkMapToCommentShouldReturnCommentFieldsFromCommentRequest() {
        String text = "good comment";
        String username = "debbie_garcia";
        CommentRequest commentRequest = CommentRequestTestBuilder.commentRequest()
                .withText(text)
                .build();
        Comment expectedComment = CommentTestBuilder.comment()
                .withText(text)
                .withUsername(username)
                .build();

        Comment actualComment = commentMapper.mapToComment(commentRequest, username);

        assertAll(() -> {
            assertThat(actualComment.getText()).isEqualTo(expectedComment.getText());
            assertThat(actualComment.getUsername()).isEqualTo(expectedComment.getUsername());
        });
    }

    @Test
    void checkMapToCommentShouldReturnNullFieldsFromCommentRequest() {
        CommentRequest commentRequest = CommentRequestTestBuilder.commentRequest()
                .withText(null)
                .build();

        Comment actualComment = commentMapper.mapToComment(commentRequest, null);

        assertAll(() -> {
            assertThat(actualComment.getText()).isNull();
            assertThat(actualComment.getUsername()).isNull();
        });
    }

    @Test
    void checkMapToNewsCommentResponseShouldReturnNewsCommentResponse() {
        String newsTitle = "news";
        String newsText = "good news";
        LocalDateTime newsTime = LocalDateTime.now();
        String newsUsername = "thomas_martinez";
        String commentText = "good comment";
        LocalDateTime commentTime = LocalDateTime.now();
        String commentUsername = "debbie_garcia";
        News news = NewsTestBuilder.news()
                .withTitle(newsTitle)
                .withText(newsText)
                .withTime(newsTime)
                .withUsername(newsUsername)
                .build();
        NewsResponse newsResponse = NewsResponseTestBuilder.newsResponse()
                .withTitle(newsTitle)
                .withText(newsText)
                .withTime(newsTime)
                .withUsername(newsUsername)
                .build();
        Comment comment = CommentTestBuilder.comment()
                .withText(commentText)
                .withTime(commentTime)
                .withUsername(commentUsername)
                .withNews(news)
                .build();
        NewsCommentResponse expectedNewsCommentResponse = NewsCommentResponseTestBuilder.newsCommentResponse()
                .withText(commentText)
                .withTime(commentTime)
                .withUsername(commentUsername)
                .withNews(newsResponse)
                .build();

        NewsCommentResponse actualNewsCommentResponse = commentMapper.mapToNewsCommentResponse(comment);

        assertThat(actualNewsCommentResponse).isEqualTo(expectedNewsCommentResponse);
    }

    @Test
    void checkMapToNewsCommentResponseShouldReturnNullNewsCommentResponse() {
        NewsCommentResponse actualNewsCommentResponse = commentMapper.mapToNewsCommentResponse(null);

        assertThat(actualNewsCommentResponse).isNull();
    }

    @Test
    void checkMapToCommentResponsesShouldReturnCommentResponses() {
        String text = "good comment";
        LocalDateTime time = LocalDateTime.now();
        String username = "debbie_garcia";
        Comment comment = CommentTestBuilder.comment()
                .withText(text)
                .withTime(time)
                .withUsername(username)
                .build();
        CommentResponse commentResponse = CommentResponseTestBuilder.commentResponse()
                .withText(text)
                .withTime(time)
                .withUsername(username)
                .build();
        List<Comment> comments = List.of(comment);
        List<CommentResponse> expectedCommentResponses = List.of(commentResponse);

        List<CommentResponse> actualCommentResponses = commentMapper.mapToCommentResponses(comments);

        assertThat(actualCommentResponses).isEqualTo(expectedCommentResponses);
    }

    @Test
    void checkMapToCommentResponsesShouldReturnEmptyCommentResponses() {
        List<Comment> comments = Collections.emptyList();

        List<CommentResponse> actualCommentResponses = commentMapper.mapToCommentResponses(comments);

        assertThat(actualCommentResponses).isEmpty();
    }

    @Test
    void checkMapToCommentResponsesShouldReturnNullCommentResponses() {
        List<CommentResponse> actualCommentResponses = commentMapper.mapToCommentResponses(null);

        assertThat(actualCommentResponses).isNull();
    }

    @Test
    void checkMapUpdateFieldsToCommentShouldReturnUpdatedText() {
        String text = "good comment";
        String updatedText = "great comment";
        Comment comment = CommentTestBuilder.comment()
                .withText(text)
                .build();
        NewsCommentRequest newsCommentRequest = NewsCommentRequestTestBuilder.newsCommentRequest()
                .withText(updatedText)
                .build();

        commentMapper.mapUpdateFieldsToComment(newsCommentRequest, comment);

        assertThat(comment.getText()).isEqualTo(newsCommentRequest.getText());
    }

    @Test
    void mapUpdateFieldsToCommentShouldReturnNotUpdatedText() {
        String text = "good comment";
        Comment comment = CommentTestBuilder.comment()
                .withText(text)
                .build();

        commentMapper.mapUpdateFieldsToComment(null, comment);

        assertThat(comment.getText()).isEqualTo(text);
    }
}
