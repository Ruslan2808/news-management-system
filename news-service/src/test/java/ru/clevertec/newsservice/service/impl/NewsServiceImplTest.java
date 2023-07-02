package ru.clevertec.newsservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import ru.clevertec.exceptionhandlingstarter.exception.NewsNotFoundException;
import ru.clevertec.newsservice.dto.filter.NewsFilter;
import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.dto.request.news.NewsTextRequest;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.mapper.NewsMapper;
import ru.clevertec.newsservice.repository.NewsRepository;
import ru.clevertec.newsservice.util.factory.SecurityContextFactory;
import ru.clevertec.newsservice.util.response.CommentNewsResponseTestBuilder;
import ru.clevertec.newsservice.util.filter.NewsFilterTestBuilder;
import ru.clevertec.newsservice.util.request.NewsRequestTestBuilder;
import ru.clevertec.newsservice.util.response.NewsResponseTestBuilder;
import ru.clevertec.newsservice.util.entity.NewsTestBuilder;
import ru.clevertec.newsservice.util.request.NewsTextRequestTestBuilder;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@ExtendWith(MockitoExtension.class)
public class NewsServiceImplTest {

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsServiceImpl newsService;

    @Nested
    class NewsServiceImplFindAllTest {

        private NewsFilter newsFilter;
        private Pageable pageable;
        private News news;
        private Example<News> newsExample;

        @BeforeEach
        void setUp() {
            newsFilter = NewsFilterTestBuilder.newsFilter().build();
            pageable = Pageable.unpaged();
            ExampleMatcher newsMatcher = ExampleMatcher.matching()
                    .withMatcher("title", contains().ignoreCase())
                    .withMatcher("text", contains().ignoreCase())
                    .withMatcher("username", contains().ignoreCase());
            news = NewsTestBuilder.news().build();
            newsExample = Example.of(news, newsMatcher);
        }

        @Test
        void checkFindAllShouldReturnSize2() {
            int expectedSize = 2;
            List<News> newsList = List.of(
                    NewsTestBuilder.news().build(),
                    NewsTestBuilder.news().build()
            );
            Page<News> newsPage = new PageImpl<>(newsList);
            List<NewsResponse> newsResponses = List.of(
                    NewsResponseTestBuilder.newsResponse().build(),
                    NewsResponseTestBuilder.newsResponse().build()
            );

            doReturn(news).when(newsMapper).mapToNews(newsFilter);
            doReturn(newsPage).when(newsRepository).findAll(newsExample, pageable);
            doReturn(newsResponses).when(newsMapper).mapToNewsResponses(newsList);

            List<NewsResponse> actualNewsResponses = newsService.findAll(newsFilter, pageable);

            assertThat(actualNewsResponses).hasSize(expectedSize);
        }

        @Test
        void checkFindAllShouldReturnNewsResponses() {
            List<News> newsList = List.of(
                    NewsTestBuilder.news().build(),
                    NewsTestBuilder.news().build()
            );
            Page<News> newsPage = new PageImpl<>(newsList);
            List<NewsResponse> newsResponses = List.of(
                    NewsResponseTestBuilder.newsResponse().build(),
                    NewsResponseTestBuilder.newsResponse().build()
            );

            doReturn(news).when(newsMapper).mapToNews(newsFilter);
            doReturn(newsPage).when(newsRepository).findAll(newsExample, pageable);
            doReturn(newsResponses).when(newsMapper).mapToNewsResponses(newsList);

            List<NewsResponse> actualNewsResponses = newsService.findAll(newsFilter, pageable);

            assertThat(actualNewsResponses).isEqualTo(newsResponses);
        }

        @Test
        void checkFindAllShouldReturnEmptyNewsResponses() {
            doReturn(news).when(newsMapper).mapToNews(newsFilter);
            doReturn(Page.empty()).when(newsRepository).findAll(newsExample, pageable);

            List<NewsResponse> actualNewsResponses = newsService.findAll(newsFilter, pageable);

            assertThat(actualNewsResponses).isEmpty();
        }
    }

    @Nested
    class NewsServiceImplFindAllByCommentsTextTest {

        private String commentText;
        private Pageable pageable;

        @BeforeEach
        void setUp() {
            commentText = "Great comment";
            pageable = Pageable.unpaged();
        }

        @Test
        void checkFindAllByCommentsTextShouldReturnSize2() {
            int expectedSize = 2;
            List<News> news = List.of(
                    NewsTestBuilder.news().build(),
                    NewsTestBuilder.news().build()
            );
            List<NewsResponse> newsResponses = List.of(
                    NewsResponseTestBuilder.newsResponse().build(),
                    NewsResponseTestBuilder.newsResponse().build()
            );

            doReturn(news).when(newsRepository).findAllByCommentsTextContainingIgnoreCase(commentText, pageable);
            doReturn(newsResponses).when(newsMapper).mapToNewsResponses(news);

            List<NewsResponse> actualNewsResponses = newsService.findAllByCommentsText(commentText, pageable);

            assertThat(actualNewsResponses).hasSize(expectedSize);
        }

        @Test
        void checkFindAllByCommentsTextShouldReturnNewsResponses() {
            List<News> news = List.of(
                    NewsTestBuilder.news().build(),
                    NewsTestBuilder.news().build()
            );
            List<NewsResponse> newsResponses = List.of(
                    NewsResponseTestBuilder.newsResponse().build(),
                    NewsResponseTestBuilder.newsResponse().build()
            );

            doReturn(news).when(newsRepository).findAllByCommentsTextContainingIgnoreCase(commentText, pageable);
            doReturn(newsResponses).when(newsMapper).mapToNewsResponses(news);

            List<NewsResponse> actualNewsResponses = newsService.findAllByCommentsText(commentText, pageable);

            assertThat(actualNewsResponses).isEqualTo(newsResponses);
        }

        @Test
        void checkFindAllByCommentsTextShouldReturnEmptyNewsResponses() {
            doReturn(Collections.emptyList()).when(newsRepository).findAllByCommentsTextContainingIgnoreCase(commentText, pageable);

            List<NewsResponse> actualNewsResponses = newsService.findAllByCommentsText(commentText, pageable);

            assertThat(actualNewsResponses).isEmpty();
        }
    }

    @Nested
    class NewsServiceImplFindAllByCommentsUsernameTest {

        private String commentUsername;
        private Pageable pageable;

        @BeforeEach
        void setUp() {
            commentUsername = "ronnie_stevens";
            pageable = Pageable.unpaged();
        }

        @Test
        void checkFindAllByCommentsUsernameShouldReturnSize2() {
            int expectedSize = 2;
            List<News> news = List.of(
                    NewsTestBuilder.news().build(),
                    NewsTestBuilder.news().build()
            );
            List<NewsResponse> newsResponses = List.of(
                    NewsResponseTestBuilder.newsResponse().build(),
                    NewsResponseTestBuilder.newsResponse().build()
            );

            doReturn(news).when(newsRepository).findAllByCommentsUsernameContainingIgnoreCase(commentUsername, pageable);
            doReturn(newsResponses).when(newsMapper).mapToNewsResponses(news);

            List<NewsResponse> actualNewsResponses = newsService.findAllByCommentsUsername(commentUsername, pageable);

            assertThat(actualNewsResponses).hasSize(expectedSize);
        }

        @Test
        void checkFindAllByCommentsUsernameShouldReturnNewsResponses() {
            List<News> news = List.of(
                    NewsTestBuilder.news().build(),
                    NewsTestBuilder.news().build()
            );
            List<NewsResponse> newsResponses = List.of(
                    NewsResponseTestBuilder.newsResponse().build(),
                    NewsResponseTestBuilder.newsResponse().build()
            );

            doReturn(news).when(newsRepository).findAllByCommentsUsernameContainingIgnoreCase(commentUsername, pageable);
            doReturn(newsResponses).when(newsMapper).mapToNewsResponses(news);

            List<NewsResponse> actualNewsResponses = newsService.findAllByCommentsUsername(commentUsername, pageable);

            assertThat(actualNewsResponses).isEqualTo(newsResponses);
        }

        @Test
        void checkFindAllByCommentsUsernameShouldReturnEmptyNewsResponses() {
            doReturn(Collections.emptyList()).when(newsRepository).findAllByCommentsUsernameContainingIgnoreCase(commentUsername, pageable);

            List<NewsResponse> actualNewsResponses = newsService.findAllByCommentsUsername(commentUsername, pageable);

            assertThat(actualNewsResponses).isEmpty();
        }
    }

    @Nested
    class NewsServiceImplFindByIdTest {

        @Test
        void checkFindByIdShouldReturnCommentNewsResponse() {
            News news = NewsTestBuilder.news().build();
            CommentNewsResponse commentNewsResponse = CommentNewsResponseTestBuilder.commentNewsResponse().build();

            doReturn(Optional.of(news)).when(newsRepository).findById(news.getId());
            doReturn(commentNewsResponse).when(newsMapper).mapToCommentNewsResponse(news);

            CommentNewsResponse actualCommentNewsResponse = newsService.findById(news.getId());

            assertThat(actualCommentNewsResponse).isEqualTo(commentNewsResponse);
        }

        @Test
        void checkFindByIdShouldThrowsNewsNotFoundException() {
            Long id = 1L;

            doReturn(Optional.empty()).when(newsRepository).findById(id);

            assertThatThrownBy(() -> newsService.findById(id))
                    .isInstanceOf(NewsNotFoundException.class);
        }
    }

    @Nested
    class NewsServiceImplSaveTest {

        private NewsRequest newsRequest;
        private Principal principal;

        @BeforeEach
        void setUp() {
            newsRequest = NewsRequestTestBuilder.newsRequest().build();
            principal = () -> "thomas_martinez";
        }

        @Test
        void checkSaveShouldReturnCommentNewsResponse() {
            News news = NewsTestBuilder.news().build();
            CommentNewsResponse commentNewsResponse = CommentNewsResponseTestBuilder.commentNewsResponse().build();

            doReturn(news).when(newsMapper).mapToNews(newsRequest, principal.getName());
            doReturn(news).when(newsRepository).save(news);
            doReturn(commentNewsResponse).when(newsMapper).mapToCommentNewsResponse(news);

            CommentNewsResponse actualCommentNewsResponse = newsService.save(newsRequest, principal);

            assertThat(actualCommentNewsResponse).isEqualTo(commentNewsResponse);
        }
    }

    @Nested
    class NewsServiceImplUpdateTest {

        private NewsRequest newsRequest;

        @BeforeEach
        void setUp() {
            String username = "thomas_martinez";
            List<String> authorities = List.of("JOURNALIST");
            SecurityContextFactory.createSecurityContext(username, authorities);
            newsRequest = NewsRequestTestBuilder.newsRequest()
                    .withTitle("Great news")
                    .withText("Text great news")
                    .build();
        }

        @Test
        void checkUpdateShouldReturnCommentNewsResponse() {
            News news = NewsTestBuilder.news()
                    .withUsername("thomas_martinez")
                    .build();
            News updatedNews = NewsTestBuilder.news()
                    .withUsername("thomas_martinez")
                    .withTitle("Great news")
                    .withText("Text great news")
                    .build();
            CommentNewsResponse commentNewsResponse = CommentNewsResponseTestBuilder.commentNewsResponse()
                    .withUsername("thomas_martinez")
                    .withTitle("Great news")
                    .withText("Text great news")
                    .build();

            doReturn(Optional.of(news)).when(newsRepository).findById(news.getId());
            doReturn(updatedNews).when(newsRepository).save(news);
            doReturn(commentNewsResponse).when(newsMapper).mapToCommentNewsResponse(updatedNews);

            CommentNewsResponse actualCommentNewsResponse = newsService.update(news.getId(), newsRequest);

            assertThat(actualCommentNewsResponse).isEqualTo(commentNewsResponse);
        }

        @Test
        void checkUpdateShouldThrowsNewsNotFoundException() {
            Long id = 1L;

            doReturn(Optional.empty()).when(newsRepository).findById(id);

            assertThatThrownBy(() -> newsService.update(id, newsRequest))
                    .isInstanceOf(NewsNotFoundException.class);
        }

        @Test
        void checkUpdateShouldThrowsAccessDeniedException() {
            News news = NewsTestBuilder.news()
                    .withUsername("sharon_hill")
                    .build();

            doReturn(Optional.of(news)).when(newsRepository).findById(news.getId());

            assertThatThrownBy(() -> newsService.update(news.getId(), newsRequest))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class NewsServiceImplUpdateTextTest {

        private NewsTextRequest newsTextRequest;

        @BeforeEach
        void setUp() {
            String username = "thomas_martinez";
            List<String> authorities = List.of("JOURNALIST");
            SecurityContextFactory.createSecurityContext(username, authorities);
            newsTextRequest = NewsTextRequestTestBuilder.newsTextRequest()
                    .withText("Text great news")
                    .build();
        }

        @Test
        void checkUpdateTextShouldReturnCommentNewsResponse() {
            News news = NewsTestBuilder.news()
                    .withUsername("thomas_martinez")
                    .build();
            News updatedNews = NewsTestBuilder.news()
                    .withUsername("thomas_martinez")
                    .withText("Text great news")
                    .build();
            CommentNewsResponse commentNewsResponse = CommentNewsResponseTestBuilder.commentNewsResponse()
                    .withUsername("thomas_martinez")
                    .withText("Text great news")
                    .build();

            doReturn(Optional.of(news)).when(newsRepository).findById(news.getId());
            doReturn(updatedNews).when(newsRepository).save(news);
            doReturn(commentNewsResponse).when(newsMapper).mapToCommentNewsResponse(updatedNews);

            CommentNewsResponse actualCommentNewsResponse = newsService.updateText(news.getId(), newsTextRequest);

            assertThat(actualCommentNewsResponse).isEqualTo(commentNewsResponse);
        }

        @Test
        void checkUpdateTextShouldThrowsNewsNotFoundException() {
            Long id = 1L;

            doReturn(Optional.empty()).when(newsRepository).findById(id);

            assertThatThrownBy(() -> newsService.updateText(id, newsTextRequest))
                    .isInstanceOf(NewsNotFoundException.class);
        }

        @Test
        void checkUpdateShouldThrowsAccessDeniedException() {
            News news = NewsTestBuilder.news()
                    .withUsername("sharon_hill")
                    .build();

            doReturn(Optional.of(news)).when(newsRepository).findById(news.getId());

            assertThatThrownBy(() -> newsService.updateText(news.getId(), newsTextRequest))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class NewsServiceImplDeleteByIdTest {

        @BeforeEach
        void setUp() {
            String username = "thomas_martinez";
            List<String> authorities = List.of("JOURNALIST");
            SecurityContextFactory.createSecurityContext(username, authorities);
        }

        @Test
        void checkDeleteByIdShouldCallDeleteById() {
            News news = NewsTestBuilder.news()
                    .withUsername("thomas_martinez")
                    .build();

            doReturn(Optional.of(news)).when(newsRepository).findById(news.getId());

            newsService.deleteById(news.getId());

            verify(newsRepository).deleteById(news.getId());
        }

        @Test
        void checkDeleteByIdShouldThrowsNewsNotFoundException() {
            Long id = 1L;

            doReturn(Optional.empty()).when(newsRepository).findById(id);

            assertThatThrownBy(() -> newsService.deleteById(id))
                    .isInstanceOf(NewsNotFoundException.class);
        }

        @Test
        void checkDeleteByIdShouldThrowsAccessDeniedException() {
            News news = NewsTestBuilder.news()
                    .withUsername("sharon_hill")
                    .build();

            doReturn(Optional.of(news)).when(newsRepository).findById(news.getId());

            assertThatThrownBy(() -> newsService.deleteById(news.getId()))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
