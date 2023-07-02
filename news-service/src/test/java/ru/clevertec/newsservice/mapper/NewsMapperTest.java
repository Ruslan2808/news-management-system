package ru.clevertec.newsservice.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mapstruct.factory.Mappers;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.clevertec.newsservice.dto.filter.NewsFilter;
import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.util.response.CommentResponseTestBuilder;
import ru.clevertec.newsservice.util.entity.CommentTestBuilder;
import ru.clevertec.newsservice.util.response.CommentNewsResponseTestBuilder;
import ru.clevertec.newsservice.util.filter.NewsFilterTestBuilder;
import ru.clevertec.newsservice.util.request.NewsRequestTestBuilder;
import ru.clevertec.newsservice.util.response.NewsResponseTestBuilder;
import ru.clevertec.newsservice.util.entity.NewsTestBuilder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertAll;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class NewsMapperTest {

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private final NewsMapper newsMapper = Mappers.getMapper(NewsMapper.class);

    @Test
    void checkMapToNewsShouldReturnNewsFieldsFromNewsFilter() {
        String title = "news";
        String text = "good news";
        String username = "thomas_martinez";
        NewsFilter newsFilter = NewsFilterTestBuilder.newsFilter()
                .withTitle(title)
                .withText(text)
                .withUsername(username)
                .build();
        News expectedNews = NewsTestBuilder.news()
                .withTitle(title)
                .withText(text)
                .withUsername(username)
                .build();

        News actualNews = newsMapper.mapToNews(newsFilter);

        assertAll(() -> {
            assertThat(actualNews.getTitle()).isEqualTo(expectedNews.getTitle());
            assertThat(actualNews.getText()).isEqualTo(expectedNews.getText());
            assertThat(actualNews.getUsername()).isEqualTo(expectedNews.getUsername());
        });
    }

    @Test
    void checkMapToNewsShouldReturnNullNews() {
        News actualNews = newsMapper.mapToNews(null);

        assertThat(actualNews).isNull();
    }

    @Test
    void checkMapToNewsShouldReturnNullNewsFieldsFromNewsFilter() {
        NewsFilter newsFilter = NewsFilterTestBuilder.newsFilter()
                .withTitle(null)
                .withUsername(null)
                .withText(null)
                .build();

        News actualNews = newsMapper.mapToNews(newsFilter);

        assertAll(() -> {
            assertThat(actualNews.getTitle()).isNull();
            assertThat(actualNews.getText()).isNull();
            assertThat(actualNews.getUsername()).isNull();
        });
    }

    @Test
    void checkMapToNewsShouldReturnNewsFieldsFromNewsRequest() {
        String title = "news";
        String text = "good news";
        String username = "thomas_martinez";
        NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                .withTitle(title)
                .withText(text)
                .build();
        News expectedNews = NewsTestBuilder.news()
                .withTitle(title)
                .withText(text)
                .withUsername(username)
                .build();

        News actualNews = newsMapper.mapToNews(newsRequest, username);

        assertAll(() -> {
            assertThat(actualNews.getTitle()).isEqualTo(expectedNews.getTitle());
            assertThat(actualNews.getText()).isEqualTo(expectedNews.getText());
            assertThat(actualNews.getUsername()).isEqualTo(expectedNews.getUsername());
        });
    }

    @Test
    void checkMapToCommentShouldReturnNullFieldsFromNewsRequest() {
        NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                .withTitle(null)
                .withText(null)
                .build();

        News actualNews = newsMapper.mapToNews(newsRequest, null);

        assertAll(() -> {
            assertThat(actualNews.getTitle()).isNull();
            assertThat(actualNews.getText()).isNull();
            assertThat(actualNews.getUsername()).isNull();
        });
    }

    @Test
    void checkMapToCommentNewsResponseShouldReturnCommentNewsResponse() {
        String newsTitle = "news";
        String newsText = "good news";
        LocalDateTime newsTime = LocalDateTime.now();
        String newsUsername = "thomas_martinez";
        String commentText = "good comment";
        LocalDateTime commentTime = LocalDateTime.now();
        String commentUsername = "debbie_garcia";
        Comment comment = CommentTestBuilder.comment()
                .withText(commentText)
                .withTime(commentTime)
                .withUsername(commentUsername)
                .build();
        List<Comment> comments = List.of(comment);
        News news = NewsTestBuilder.news()
                .withTitle(newsTitle)
                .withText(newsText)
                .withTime(newsTime)
                .withUsername(newsUsername)
                .withComments(comments)
                .build();
        CommentResponse commentResponse = CommentResponseTestBuilder.commentResponse()
                .withText(commentText)
                .withTime(commentTime)
                .withUsername(commentUsername)
                .build();
        List<CommentResponse> commentResponses = List.of(commentResponse);
        CommentNewsResponse expectedCommentNewsResponse = CommentNewsResponseTestBuilder.commentNewsResponse()
                .withTitle(newsTitle)
                .withText(newsText)
                .withTime(newsTime)
                .withUsername(newsUsername)
                .withComments(commentResponses)
                .build();

        doReturn(commentResponses).when(commentMapper).mapToCommentResponses(comments);

        CommentNewsResponse actualCommentNewsResponse = newsMapper.mapToCommentNewsResponse(news);

        assertThat(actualCommentNewsResponse).isEqualTo(expectedCommentNewsResponse);
    }

    @Test
    void checkMapToCommentNewsResponseShouldReturnNullCommentNewsResponse() {
        CommentNewsResponse actualCommentNewsResponse = newsMapper.mapToCommentNewsResponse(null);

        assertThat(actualCommentNewsResponse).isNull();
    }

    @Test
    void checkMapToCommentResponsesShouldReturnCommentResponses() {
        String title = "news";
        String text = "good news";
        LocalDateTime time = LocalDateTime.now();
        String username = "thomas_martinez";
        News news = NewsTestBuilder.news()
                .withTitle(title)
                .withText(text)
                .withTime(time)
                .withUsername(username)
                .build();
        NewsResponse newsResponse = NewsResponseTestBuilder.newsResponse()
                .withTitle(title)
                .withText(text)
                .withTime(time)
                .withUsername(username)
                .build();
        List<News> newsList = List.of(news);
        List<NewsResponse> expectedNewsResponses = List.of(newsResponse);

        List<NewsResponse> actualNewsResponses = newsMapper.mapToNewsResponses(newsList);

        assertThat(actualNewsResponses).isEqualTo(expectedNewsResponses);
    }

    @Test
    void checkMapToNewsResponsesShouldReturnEmptyNewsResponses() {
        List<News> news = Collections.emptyList();

        List<NewsResponse> actualNewsResponse = newsMapper.mapToNewsResponses(news);

        assertThat(actualNewsResponse).isEmpty();
    }

    @Test
    void checkMapToNewsResponsesShouldReturnNullNewsResponses() {
        List<NewsResponse> actualNewsResponse = newsMapper.mapToNewsResponses(null);

        assertThat(actualNewsResponse).isNull();
    }

    @Test
    void checkMapUpdateFieldsToNewsShouldReturnUpdatedFields() {
        String title = "today news";
        String text = "good news";
        String updatedTitle = "yesterday news";
        String updatedText = "great news";
        News news = NewsTestBuilder.news()
                .withTitle(title)
                .withText(text)
                .build();
        NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                .withTitle(updatedTitle)
                .withText(updatedText)
                .build();

        newsMapper.mapUpdateFieldsToNews(newsRequest, news);

        assertAll(() -> {
            assertThat(news.getTitle()).isEqualTo(newsRequest.getTitle());
            assertThat(news.getText()).isEqualTo(newsRequest.getText());
        });
    }

    @Test
    void checkMapUpdateFieldsToNewsShouldReturnNotUpdatedFields() {
        String title = "news";
        String text = "good news";
        News news = NewsTestBuilder.news()
                .withTitle(title)
                .withText(text)
                .build();

        newsMapper.mapUpdateFieldsToNews(null, news);

        assertAll(() -> {
            assertThat(news.getTitle()).isEqualTo(title);
            assertThat(news.getText()).isEqualTo(text);
        });
    }

    @Test
    void checkMapUpdateTextFieldToNewsShouldReturnUpdatedTextField() {
        String text = "good news";
        String updatedText = "great news";
        News news = NewsTestBuilder.news()
                .withText(text)
                .build();
        NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                .withText(updatedText)
                .build();

        newsMapper.mapUpdateFieldsToNews(newsRequest, news);

        assertThat(news.getText()).isEqualTo(newsRequest.getText());
    }

    @Test
    void checkMapUpdateTextFieldToNewsShouldReturnNotUpdatedTextField() {
        String text = "good news";
        News news = NewsTestBuilder.news()
                .withText(text)
                .build();

        newsMapper.mapUpdateFieldsToNews(null, news);

        assertThat(news.getText()).isEqualTo(text);
    }
}
