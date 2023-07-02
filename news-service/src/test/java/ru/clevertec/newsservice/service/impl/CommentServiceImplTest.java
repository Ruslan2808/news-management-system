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

import ru.clevertec.exceptionhandlingstarter.exception.CommentNotFoundException;
import ru.clevertec.exceptionhandlingstarter.exception.NewsNotFoundException;
import ru.clevertec.newsservice.dto.filter.CommentFilter;
import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;
import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.mapper.CommentMapper;
import ru.clevertec.newsservice.repository.CommentRepository;
import ru.clevertec.newsservice.repository.NewsRepository;
import ru.clevertec.newsservice.util.factory.SecurityContextFactory;
import ru.clevertec.newsservice.util.filter.CommentFilterTestBuilder;
import ru.clevertec.newsservice.util.request.CommentRequestTestBuilder;
import ru.clevertec.newsservice.util.response.CommentResponseTestBuilder;
import ru.clevertec.newsservice.util.entity.CommentTestBuilder;
import ru.clevertec.newsservice.util.request.NewsCommentRequestTestBuilder;
import ru.clevertec.newsservice.util.response.NewsCommentResponseTestBuilder;
import ru.clevertec.newsservice.util.entity.NewsTestBuilder;

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
class CommentServiceImplTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Nested
    class CommentServiceImplFindAllTest {

        private CommentFilter commentFilter;
        private Pageable pageable;
        private Comment comment;
        private Example<Comment> commentExample;

        @BeforeEach
        void setUp() {
            commentFilter = CommentFilterTestBuilder.commentFilter().build();
            pageable = Pageable.unpaged();
            ExampleMatcher commentMatcher = ExampleMatcher.matching()
                    .withMatcher("text", contains().ignoreCase())
                    .withMatcher("username", contains().ignoreCase());
            comment = CommentTestBuilder.comment().build();
            commentExample = Example.of(comment, commentMatcher);
        }

        @Test
        void checkFindAllShouldReturnSize2() {
            int expectedSize = 2;
            List<Comment> comments = List.of(
                    CommentTestBuilder.comment().build(),
                    CommentTestBuilder.comment().build()
            );
            Page<Comment> commentPage = new PageImpl<>(comments);
            List<CommentResponse> commentResponses = List.of(
                    CommentResponseTestBuilder.commentResponse().build(),
                    CommentResponseTestBuilder.commentResponse().build()
            );

            doReturn(comment).when(commentMapper).mapToComment(commentFilter);
            doReturn(commentPage).when(commentRepository).findAll(commentExample, pageable);
            doReturn(commentResponses).when(commentMapper).mapToCommentResponses(comments);

            List<CommentResponse> actualCommentResponses = commentService.findAll(commentFilter, pageable);

            assertThat(actualCommentResponses).hasSize(expectedSize);
        }

        @Test
        void checkFindAllShouldReturnCommentResponses() {
            List<Comment> comments = List.of(
                    CommentTestBuilder.comment().build(),
                    CommentTestBuilder.comment().build()
            );
            Page<Comment> commentPage = new PageImpl<>(comments);
            List<CommentResponse> commentResponses = List.of(
                    CommentResponseTestBuilder.commentResponse().build(),
                    CommentResponseTestBuilder.commentResponse().build()
            );

            doReturn(comment).when(commentMapper).mapToComment(commentFilter);
            doReturn(commentPage).when(commentRepository).findAll(commentExample, pageable);
            doReturn(commentResponses).when(commentMapper).mapToCommentResponses(comments);

            List<CommentResponse> actualCommentResponses = commentService.findAll(commentFilter, pageable);

            assertThat(actualCommentResponses).isEqualTo(commentResponses);
        }

        @Test
        void checkFindAllShouldReturnEmptyCommentResponses() {
            doReturn(comment).when(commentMapper).mapToComment(commentFilter);
            doReturn(Page.empty()).when(commentRepository).findAll(commentExample, pageable);

            List<CommentResponse> actualCommentResponses = commentService.findAll(commentFilter, pageable);

            assertThat(actualCommentResponses).isEmpty();
        }
    }

    @Nested
    class CommentServiceImplFindAllByNewsIdTest {

        private Long id;
        private Pageable pageable;

        @BeforeEach
        void setUp() {
            id = 1L;
            pageable = Pageable.unpaged();
        }

        @Test
        void checkFindAllByNewsIdShouldReturnSize2() {
            int expectedSize = 2;
            List<Comment> comments = List.of(
                    CommentTestBuilder.comment().build(),
                    CommentTestBuilder.comment().build()
            );
            List<CommentResponse> commentResponses = List.of(
                    CommentResponseTestBuilder.commentResponse().build(),
                    CommentResponseTestBuilder.commentResponse().build()
            );

            doReturn(comments).when(commentRepository).findAllByNewsId(id, pageable);
            doReturn(commentResponses).when(commentMapper).mapToCommentResponses(comments);

            List<CommentResponse> actualCommentResponses = commentService.findAllByNewsId(id, pageable);

            assertThat(actualCommentResponses).hasSize(expectedSize);
        }

        @Test
        void checkFindAllByNewsIdShouldReturnCommentResponses() {
            List<Comment> comments = List.of(
                    CommentTestBuilder.comment().build(),
                    CommentTestBuilder.comment().build()
            );
            List<CommentResponse> commentResponses = List.of(
                    CommentResponseTestBuilder.commentResponse().build(),
                    CommentResponseTestBuilder.commentResponse().build()
            );

            doReturn(comments).when(commentRepository).findAllByNewsId(id, pageable);
            doReturn(commentResponses).when(commentMapper).mapToCommentResponses(comments);

            List<CommentResponse> actualCommentResponses = commentService.findAllByNewsId(id, pageable);

            assertThat(actualCommentResponses).isEqualTo(commentResponses);
        }

        @Test
        void checkFindAllByNewsIdShouldReturnEmptyCommentResponses() {
            doReturn(Collections.emptyList()).when(commentRepository).findAllByNewsId(id, pageable);

            List<CommentResponse> actualCommentResponses = commentService.findAllByNewsId(id, pageable);

            assertThat(actualCommentResponses).isEmpty();
        }
    }

    @Nested
    class CommentServiceImplFindByIdTest {

        @Test
        void checkFindByIdShouldReturnNewsCommentResponse() {
            Comment comment = CommentTestBuilder.comment().build();
            NewsCommentResponse newsCommentResponse = NewsCommentResponseTestBuilder.newsCommentResponse().build();

            doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
            doReturn(newsCommentResponse).when(commentMapper).mapToNewsCommentResponse(comment);

            NewsCommentResponse actualNewsCommentResponse = commentService.findById(comment.getId());

            assertThat(actualNewsCommentResponse).isEqualTo(newsCommentResponse);
        }

        @Test
        void checkFindByIdShouldThrowsCommentNotFoundException() {
            Long id = 1L;

            doReturn(Optional.empty()).when(commentRepository).findById(id);

            assertThatThrownBy(() -> commentService.findById(id))
                    .isInstanceOf(CommentNotFoundException.class);
        }
    }

    @Nested
    class CommentServiceImplFindByIdAndNewsIdTest {

        @Test
        void checkFindByIdAndNewsIdShouldReturnNewsCommentResponse() {
            Long newsId = 1L;
            Comment comment = CommentTestBuilder.comment().build();
            NewsCommentResponse newsCommentResponse = NewsCommentResponseTestBuilder.newsCommentResponse().build();

            doReturn(Optional.of(comment)).when(commentRepository).findByIdAndNewsId(comment.getId(), newsId);
            doReturn(newsCommentResponse).when(commentMapper).mapToNewsCommentResponse(comment);

            NewsCommentResponse actualNewsCommentResponse = commentService.findByIdAndNewsId(comment.getId(), newsId);

            assertThat(actualNewsCommentResponse).isEqualTo(newsCommentResponse);
        }

        @Test
        void checkFindByIdAndNewsIdShouldThrowsCommentNotFoundException() {
            Long commentId = 1L;
            Long newsId = 1L;

            doReturn(Optional.empty()).when(commentRepository).findByIdAndNewsId(commentId, newsId);

            assertThatThrownBy(() -> commentService.findByIdAndNewsId(commentId, newsId))
                    .isInstanceOf(CommentNotFoundException.class);
        }
    }

    @Nested
    class CommentServiceImplSaveTest {

        private CommentRequest commentRequest;
        private Principal principal;

        @BeforeEach
        void setUp() {
            commentRequest = CommentRequestTestBuilder.commentRequest().build();
            principal = () -> "ronnie_stevens";
        }

        @Test
        void checkSaveShouldReturnNewsCommentResponse() {
            Comment comment = CommentTestBuilder.comment().build();
            News news = NewsTestBuilder.news().build();
            NewsCommentResponse newsCommentResponse = NewsCommentResponseTestBuilder.newsCommentResponse().build();

            doReturn(Optional.of(news)).when(newsRepository).findById(news.getId());
            doReturn(comment).when(commentMapper).mapToComment(commentRequest, principal.getName());
            doReturn(comment).when(commentRepository).save(comment);
            doReturn(newsCommentResponse).when(commentMapper).mapToNewsCommentResponse(comment);

            NewsCommentResponse actualNewsCommentResponse = commentService.save(commentRequest, principal);

            assertThat(actualNewsCommentResponse).isEqualTo(newsCommentResponse);
        }

        @Test
        void checkSaveShouldThrowsNewsNotFoundException() {
            Long id = 1L;

            doReturn(Optional.empty()).when(newsRepository).findById(id);

            assertThatThrownBy(() -> commentService.save(commentRequest, principal))
                    .isInstanceOf(NewsNotFoundException.class);
        }
    }

    @Nested
    class CommentServiceImplUpdateTest {

        private NewsCommentRequest newsCommentRequest;

        @BeforeEach
        void setUp() {
            String username = "ronnie_stevens";
            List<String> authorities = List.of("SUBSCRIBER");
            SecurityContextFactory.createSecurityContext(username, authorities);
            newsCommentRequest = NewsCommentRequestTestBuilder.newsCommentRequest()
                    .withText("Great comment")
                    .build();
        }

        @Test
        void checkUpdateShouldReturnNewsCommentResponse() {
            Comment comment = CommentTestBuilder.comment()
                    .withUsername("ronnie_stevens")
                    .build();
            Comment updatedComment = CommentTestBuilder.comment()
                    .withUsername("ronnie_stevens")
                    .withText("Great comment")
                    .build();
            NewsCommentResponse newsCommentResponse = NewsCommentResponseTestBuilder.newsCommentResponse()
                    .withUsername("ronnie_stevens")
                    .withText("Great comment")
                    .build();

            doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
            doReturn(updatedComment).when(commentRepository).save(comment);
            doReturn(newsCommentResponse).when(commentMapper).mapToNewsCommentResponse(updatedComment);

            NewsCommentResponse actualNewsCommentResponse = commentService.update(comment.getId(), newsCommentRequest);

            assertThat(actualNewsCommentResponse).isEqualTo(newsCommentResponse);
        }

        @Test
        void checkUpdateShouldThrowsCommentNotFoundException() {
            Long id = 1L;

            doReturn(Optional.empty()).when(commentRepository).findById(id);

            assertThatThrownBy(() -> commentService.update(id, newsCommentRequest))
                    .isInstanceOf(CommentNotFoundException.class);
        }

        @Test
        void checkUpdateShouldThrowsAccessDeniedException() {
            Comment comment = CommentTestBuilder.comment()
                    .withUsername("debbie_garcia")
                    .build();

            doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());

            assertThatThrownBy(() -> commentService.update(comment.getId(), newsCommentRequest))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class CommentServiceImplDeleteByIdTest {

        @BeforeEach
        void setUp() {
            String username = "ronnie_stevens";
            List<String> authorities = List.of("SUBSCRIBER");
            SecurityContextFactory.createSecurityContext(username, authorities);
        }

        @Test
        void checkDeleteByIdShouldCallDeleteById() {
            Comment comment = CommentTestBuilder.comment()
                    .withUsername("ronnie_stevens")
                    .build();

            doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());

            commentService.deleteById(comment.getId());

            verify(commentRepository).deleteById(comment.getId());
        }

        @Test
        void checkDeleteByIdShouldThrowsCommentNotFoundException() {
            Long id = 1L;

            doReturn(Optional.empty()).when(commentRepository).findById(id);

            assertThatThrownBy(() -> commentService.deleteById(id))
                    .isInstanceOf(CommentNotFoundException.class);
        }

        @Test
        void checkDeleteByIdShouldThrowsAccessDeniedException() {
            Comment comment = CommentTestBuilder.comment()
                    .withUsername("debbie_garcia")
                    .build();

            doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());

            assertThatThrownBy(() -> commentService.deleteById(comment.getId()))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
