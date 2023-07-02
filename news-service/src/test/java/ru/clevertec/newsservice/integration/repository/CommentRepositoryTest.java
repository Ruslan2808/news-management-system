package ru.clevertec.newsservice.integration.repository;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.integration.BaseIntegrationTest;
import ru.clevertec.newsservice.repository.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

class CommentRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Nested
    class CommentRepositoryFindAllByNewsIdTest {

        @ParameterizedTest
        @MethodSource("provideNewsIdAndExpectedSizes")
        void checkFindAllByNewsIdShouldReturnSize(Long newsId, int expectedSize) {
            Pageable pageable = Pageable.unpaged();

            List<Comment> actualComments = commentRepository.findAllByNewsId(newsId, pageable);

            assertThat(actualComments).hasSize(expectedSize);
        }

        @ParameterizedTest
        @MethodSource("provideNewsIdAndExpectedCommentIds")
        void checkFindAllByNewsIdShouldReturnCommentIds(Long newsId, List<Long> expectedCommentIds) {
            Pageable pageable = Pageable.unpaged();

            List<Comment> actualComments = commentRepository.findAllByNewsId(newsId, pageable);
            List<Long> actualCommentIds = actualComments.stream()
                    .map(Comment::getId)
                    .toList();

            assertThat(actualCommentIds).containsAll(expectedCommentIds);
        }

        @ParameterizedTest
        @MethodSource("provideNewsIdAndPaginationParamsAndExpectedSizes")
        void checkFindAllByNewsIdShouldReturnSizeByPaginationParams(Long newsId, Pageable pageable, int expectedSize) {
            List<Comment> actualComments = commentRepository.findAllByNewsId(newsId, pageable);

            assertThat(actualComments).hasSize(expectedSize);
        }

        @ParameterizedTest
        @MethodSource("provideNewsIdAndSortingParamsAndCommentIds")
        void checkFindAllByNewsIdShouldReturnCommentIdsBySortingParams(Long newsId, Pageable pageable, Long[] commentIds) {
            List<Comment> actualComments = commentRepository.findAllByNewsId(newsId, pageable);
            List<Long> actualCommentIds = actualComments.stream()
                    .map(Comment::getId)
                    .toList();

            assertThat(actualCommentIds).containsExactly(commentIds);
        }

        @ParameterizedTest
        @MethodSource("provideNewsIdAndPaginationParamsEmptyComments")
        void checkFindAllByNewsIdShouldReturnEmptyCommentsByPaginationParams(Long newsId, Pageable pageable) {
            List<Comment> actualComments = commentRepository.findAllByNewsId(newsId, pageable);

            assertThat(actualComments).isEmpty();
        }

        @Test
        void checkFindAllByNewsIdShouldReturnEmptyComments() {
            Long newsId = 11L;
            Pageable pageable = Pageable.unpaged();

            List<Comment> actualComments = commentRepository.findAllByNewsId(newsId, pageable);

            assertThat(actualComments).isEmpty();
        }

        private static Stream<Arguments> provideNewsIdAndExpectedSizes() {
            return Stream.of(
                    arguments(1L, 2),
                    arguments(2L, 1),
                    arguments(3L, 3),
                    arguments(4L, 2),
                    arguments(5L, 2)
            );
        }

        private static Stream<Arguments> provideNewsIdAndExpectedCommentIds() {
            return Stream.of(
                    arguments(1L, List.of(1L, 2L)),
                    arguments(2L, List.of(3L)),
                    arguments(3L, List.of(4L, 5L, 6L)),
                    arguments(4L, List.of(7L, 8L)),
                    arguments(5L, List.of(9L, 10L))
            );
        }

        private static Stream<Arguments> provideNewsIdAndPaginationParamsAndExpectedSizes() {
            return Stream.of(
                    arguments(1L, PageRequest.of(0, 1), 1),
                    arguments(1L, PageRequest.of(0, 2), 2),
                    arguments(1L, PageRequest.of(1, 1), 1),
                    arguments(2L, PageRequest.of(0, 1), 1),
                    arguments(3L, PageRequest.of(0, 1), 1),
                    arguments(3L, PageRequest.of(0, 2), 2),
                    arguments(3L, PageRequest.of(0, 3), 3),
                    arguments(3L, PageRequest.of(1, 1), 1),
                    arguments(3L, PageRequest.of(2, 1), 1),
                    arguments(4L, PageRequest.of(0, 1), 1),
                    arguments(4L, PageRequest.of(0, 2), 2),
                    arguments(4L, PageRequest.of(1, 1), 1),
                    arguments(5L, PageRequest.of(0, 1), 1),
                    arguments(5L, PageRequest.of(0, 2), 2),
                    arguments(5L, PageRequest.of(1, 1), 1)
            );
        }

        private static Stream<Arguments> provideNewsIdAndSortingParamsAndCommentIds() {
            return Stream.of(
                    arguments(1L, PageRequest.of(0, 2, Sort.by(ASC, "id")), new Long[]{1L, 2L}),
                    arguments(1L, PageRequest.of(0, 2, Sort.by(DESC, "id")), new Long[]{2L, 1L}),
                    arguments(1L, PageRequest.of(0, 2, Sort.by(ASC, "text")), new Long[]{1L, 2L}),
                    arguments(1L, PageRequest.of(0, 2, Sort.by(DESC, "text")), new Long[]{2L, 1L}),
                    arguments(1L, PageRequest.of(0, 2, Sort.by(ASC, "time")), new Long[]{2L, 1L}),
                    arguments(1L, PageRequest.of(0, 2, Sort.by(DESC, "time")), new Long[]{1L, 2L}),
                    arguments(1L, PageRequest.of(0, 2, Sort.by(ASC, "username")), new Long[]{1L, 2L}),
                    arguments(1L, PageRequest.of(0, 2, Sort.by(DESC, "username")),new Long[]{2L, 1L}),
                    arguments(2L, PageRequest.of(0, 1, Sort.by(ASC, "id")), new Long[]{3L}),
                    arguments(2L, PageRequest.of(0, 1, Sort.by(ASC, "text")), new Long[]{3L}),
                    arguments(2L, PageRequest.of(0, 1, Sort.by(ASC, "time")), new Long[]{3L}),
                    arguments(2L, PageRequest.of(0, 1, Sort.by(ASC, "username")), new Long[]{3L}),
                    arguments(3L, PageRequest.of(0, 3, Sort.by(ASC, "id")), new Long[]{4L, 5L, 6L}),
                    arguments(3L, PageRequest.of(0, 3, Sort.by(DESC, "id")), new Long[]{6L, 5L, 4L}),
                    arguments(3L, PageRequest.of(0, 3, Sort.by(ASC, "text")), new Long[]{4L, 6L, 5L}),
                    arguments(3L, PageRequest.of(0, 3, Sort.by(DESC, "text")), new Long[]{5L, 6L, 4L}),
                    arguments(3L, PageRequest.of(0, 3, Sort.by(ASC, "time")), new Long[]{4L, 5L, 6L}),
                    arguments(3L, PageRequest.of(0, 3, Sort.by(DESC, "time")), new Long[]{6L, 5L, 4L}),
                    arguments(3L, PageRequest.of(0, 3, Sort.by(ASC, "username")), new Long[]{4L, 5L, 6L}),
                    arguments(3L, PageRequest.of(0, 3, Sort.by(DESC, "username")), new Long[]{6L, 5L, 4L}),
                    arguments(4L, PageRequest.of(0, 2, Sort.by(ASC, "id")), new Long[]{7L, 8L}),
                    arguments(4L, PageRequest.of(0, 2, Sort.by(DESC, "id")), new Long[]{8L, 7L}),
                    arguments(4L, PageRequest.of(0, 2, Sort.by(ASC, "text")), new Long[]{8L, 7L}),
                    arguments(4L, PageRequest.of(0, 2, Sort.by(DESC, "text")), new Long[]{7L, 8L}),
                    arguments(4L, PageRequest.of(0, 2, Sort.by(ASC, "time")), new Long[]{8L, 7L}),
                    arguments(4L, PageRequest.of(0, 2, Sort.by(DESC, "time")), new Long[]{7L, 8L}),
                    arguments(4L, PageRequest.of(0, 2, Sort.by(ASC, "username")), new Long[]{7L, 8L}),
                    arguments(4L, PageRequest.of(0, 2, Sort.by(DESC, "username")),new Long[]{8L, 7L}),
                    arguments(5L, PageRequest.of(0, 2, Sort.by(ASC, "id")), new Long[]{9L, 10L}),
                    arguments(5L, PageRequest.of(0, 2, Sort.by(DESC, "id")), new Long[]{10L, 9L}),
                    arguments(5L, PageRequest.of(0, 2, Sort.by(ASC, "text")), new Long[]{10L, 9L}),
                    arguments(5L, PageRequest.of(0, 2, Sort.by(DESC, "text")), new Long[]{9L, 10L}),
                    arguments(5L, PageRequest.of(0, 2, Sort.by(ASC, "time")), new Long[]{9L, 10L}),
                    arguments(5L, PageRequest.of(0, 2, Sort.by(DESC, "time")), new Long[]{10L, 9L}),
                    arguments(5L, PageRequest.of(0, 2, Sort.by(ASC, "username")), new Long[]{9L, 10L}),
                    arguments(5L, PageRequest.of(0, 2, Sort.by(DESC, "username")),new Long[]{9L, 10L})
            );
        }

        private static Stream<Arguments> provideNewsIdAndPaginationParamsEmptyComments() {
            return Stream.of(
                    arguments(1L, PageRequest.of(1, 2)),
                    arguments(1L, PageRequest.of(2, 1)),
                    arguments(2L, PageRequest.of(1, 1)),
                    arguments(3L, PageRequest.of(1, 3)),
                    arguments(3L, PageRequest.of(2, 2)),
                    arguments(3L, PageRequest.of(3, 1)),
                    arguments(4L, PageRequest.of(1, 2)),
                    arguments(4L, PageRequest.of(2, 1)),
                    arguments(5L, PageRequest.of(1, 2)),
                    arguments(5L, PageRequest.of(2, 1))
            );
        }
    }

    @Nested
    class CommentRepositoryFindByIdAndNewsIdTest {

        @ParameterizedTest
        @MethodSource("provideCommentIdAndNewsIdComments")
        void checkFindByIdAndNewsIdShouldReturnComment(Long commentId, Long newsId) {
            Optional<Comment> actualComment = commentRepository.findByIdAndNewsId(commentId, newsId);

            assertThat(actualComment).isPresent();
            assertThat(actualComment.get().getId()).isEqualTo(commentId);
        }

        @ParameterizedTest
        @MethodSource("provideCommentIdAndNewsIdNotPresentComments")
        void checkFindByIdAndNewsIdShouldReturnNotPresentComment(Long commentId, Long newsId) {
            Optional<Comment> actualComment = commentRepository.findByIdAndNewsId(commentId, newsId);

            assertThat(actualComment).isNotPresent();
        }

        private static Stream<Arguments> provideCommentIdAndNewsIdComments() {
            return Stream.of(
                    arguments(1L, 1L),
                    arguments(2L, 1L),
                    arguments(3L, 2L),
                    arguments(4L, 3L),
                    arguments(5L, 3L),
                    arguments(6L, 3L),
                    arguments(7L, 4L),
                    arguments(8L, 4L),
                    arguments(9L, 5L),
                    arguments(10L, 5L)
            );
        }

        private static Stream<Arguments> provideCommentIdAndNewsIdNotPresentComments() {
            return Stream.of(
                    arguments(0L, 0L),
                    arguments(0L, 1L),
                    arguments(0L, 2L),
                    arguments(1L, 2L),
                    arguments(2L, 2L),
                    arguments(0L, 3L),
                    arguments(1L, 3L),
                    arguments(2L, 3L),
                    arguments(3L, 3L),
                    arguments(0L, 4L),
                    arguments(1L, 4L),
                    arguments(2L, 4L),
                    arguments(3L, 4L),
                    arguments(4L, 4L),
                    arguments(5L, 4L),
                    arguments(6L, 4L),
                    arguments(0L, 5L),
                    arguments(1L, 5L),
                    arguments(2L, 5L),
                    arguments(3L, 5L),
                    arguments(4L, 5L),
                    arguments(5L, 5L),
                    arguments(6L, 5L),
                    arguments(7L, 5L),
                    arguments(8L, 5L)
            );
        }
    }
}
