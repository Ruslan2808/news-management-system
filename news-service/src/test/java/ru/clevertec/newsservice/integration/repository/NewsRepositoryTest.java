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

import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.integration.BaseIntegrationTest;
import ru.clevertec.newsservice.repository.NewsRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

class NewsRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Nested
    class NewsRepositoryFindAllByCommentsTextContainingIgnoreCaseTest {

        @ParameterizedTest
        @MethodSource("provideCommentsTextAndExpectedSizes")
        void checkFindAllByCommentsTextContainingIgnoreCaseShouldReturnSize(String commentsText, int expectedSize) {
            Pageable pageable = Pageable.unpaged();

            List<News> actualNews = newsRepository.findAllByCommentsTextContainingIgnoreCase(commentsText, pageable);

            assertThat(actualNews).hasSize(expectedSize);
        }

        @ParameterizedTest
        @MethodSource("provideCommentsTextAndNewsIds")
        void checkFindAllByCommentsTextContainingIgnoreShouldCaseReturnNewsIds(String commentsText, List<Long> newsIds) {
            Pageable pageable = Pageable.unpaged();

            List<News> actualNews = newsRepository.findAllByCommentsTextContainingIgnoreCase(commentsText, pageable);
            List<Long> actualNewsIds = actualNews.stream()
                    .map(News::getId)
                    .toList();

            assertThat(actualNewsIds).containsAll(newsIds);
        }

        @ParameterizedTest
        @MethodSource("provideCommentsTextAndPaginationParamsAndExpectedSizes")
        void checkFindAllByCommentsTextContainingIgnoreCaseShouldReturnSizeByPaginationParams(String commentsText,
                                                                                              Pageable pageable,
                                                                                              int expectedSize) {
            List<News> actualNews = newsRepository.findAllByCommentsTextContainingIgnoreCase(commentsText, pageable);

            assertThat(actualNews).hasSize(expectedSize);
        }

        @ParameterizedTest
        @MethodSource("provideCommentsTextAndSortingParamsAndNewsIds")
        void checkFindAllByCommentsTextContainingIgnoreCaseShouldReturnCommentIdsBySortingParams(String commentsText,
                                                                                                 Pageable pageable,
                                                                                                 Long[] newsIds) {
            List<News> actualNews = newsRepository.findAllByCommentsTextContainingIgnoreCase(commentsText, pageable);
            List<Long> actualNewsIds = actualNews.stream()
                    .map(News::getId)
                    .toList();

            assertThat(actualNewsIds).containsExactly(newsIds);
        }

        @ParameterizedTest
        @MethodSource("provideCommentsTextAndPaginationParamsEmptyNews")
        void checkFindAllByCommentsTextContainingIgnoreCaseShouldReturnEmptyNewsByPaginationParams(String commentsText,
                                                                                                   Pageable pageable) {
            List<News> actualNews = newsRepository.findAllByCommentsTextContainingIgnoreCase(commentsText, pageable);

            assertThat(actualNews).isEmpty();
        }

        @Test
        void checkFindAllByCommentsTextContainingIgnoreCaseShouldReturnEmptyNews() {
            String commentsText = "Good news";
            Pageable pageable = Pageable.unpaged();

            List<News> actualNews = newsRepository.findAllByCommentsTextContainingIgnoreCase(commentsText, pageable);

            assertThat(actualNews).isEmpty();
        }

        private static Stream<Arguments> provideCommentsTextAndExpectedSizes() {
            return Stream.of(
                    arguments("game", 2),
                    arguments("GAME", 2),
                    arguments("this", 3),
                    arguments("THIS", 3),
                    arguments("great", 2),
                    arguments("GREAT", 2),
                    arguments("record", 1),
                    arguments("RECORD", 1),
                    arguments("human", 1),
                    arguments("HUMAN", 1)
            );
        }

        private static Stream<Arguments> provideCommentsTextAndNewsIds() {
            return Stream.of(
                    arguments("game", List.of(3L, 4L)),
                    arguments("GAME", List.of(3L, 4L)),
                    arguments("this", List.of(1L, 3L, 5L)),
                    arguments("THIS", List.of(1L, 3L, 5L)),
                    arguments("great", List.of(3L, 4L)),
                    arguments("GREAT", List.of(3L, 4L)),
                    arguments("record", List.of(5L)),
                    arguments("RECORD", List.of(5L)),
                    arguments("human",  List.of(5L)),
                    arguments("HUMAN",  List.of(5L))
            );
        }

        private static Stream<Arguments> provideCommentsTextAndPaginationParamsAndExpectedSizes() {
            return Stream.of(
                    arguments("game", PageRequest.of(0, 1), 1),
                    arguments("GAME", PageRequest.of(0, 2), 2),
                    arguments("gAmE", PageRequest.of(1, 1), 1),
                    arguments("this", PageRequest.of(0, 1), 1),
                    arguments("THIS", PageRequest.of(0, 2), 2),
                    arguments("tHiS", PageRequest.of(0, 3), 3),
                    arguments("ThIs", PageRequest.of(1, 1), 1),
                    arguments("THiS", PageRequest.of(2, 1), 1),
                    arguments("great", PageRequest.of(0, 1), 1),
                    arguments("GREAT", PageRequest.of(0, 2), 2),
                    arguments("gReAt", PageRequest.of(1, 1), 1)
            );
        }

        private static Stream<Arguments> provideCommentsTextAndSortingParamsAndNewsIds() {
            return Stream.of(
                    arguments("game", PageRequest.of(0, 2, Sort.by(ASC, "id")), new Long[]{3L, 4L}),
                    arguments("GAME", PageRequest.of(0, 2, Sort.by(DESC, "id")), new Long[]{4L, 3L}),
                    arguments("gAmE", PageRequest.of(0, 2, Sort.by(ASC, "title")), new Long[]{3L, 4L}),
                    arguments("GaMe", PageRequest.of(0, 2, Sort.by(DESC, "title")), new Long[]{4L, 3L}),
                    arguments("GAMe", PageRequest.of(0, 2, Sort.by(ASC, "text")), new Long[]{3L, 4L}),
                    arguments("gamE", PageRequest.of(0, 2, Sort.by(DESC, "text")), new Long[]{4L, 3L}),
                    arguments("gaME", PageRequest.of(0, 2, Sort.by(ASC, "time")), new Long[]{3L, 4L}),
                    arguments("GAme", PageRequest.of(0, 2, Sort.by(DESC, "time")), new Long[]{4L, 3L}),
                    arguments("gAME", PageRequest.of(0, 2, Sort.by(ASC, "username")), new Long[]{3L, 4L}),
                    arguments("game", PageRequest.of(0, 2, Sort.by(DESC, "username")), new Long[]{4L, 3L}),
                    arguments("this", PageRequest.of(0, 3, Sort.by(ASC, "id")), new Long[]{1L, 3L, 5L}),
                    arguments("THIS", PageRequest.of(0, 3, Sort.by(DESC, "id")), new Long[]{5L, 3L, 1L}),
                    arguments("tHiS", PageRequest.of(0, 3, Sort.by(ASC, "title")), new Long[]{3L, 5L, 1L}),
                    arguments("ThIs", PageRequest.of(0, 3, Sort.by(DESC, "title")), new Long[]{1L, 5L, 3L}),
                    arguments("THIs", PageRequest.of(0, 3, Sort.by(ASC, "text")), new Long[]{3L, 1L, 5L}),
                    arguments("tHIS", PageRequest.of(0, 3, Sort.by(DESC, "text")), new Long[]{5L, 1L, 3L}),
                    arguments("THis", PageRequest.of(0, 3, Sort.by(ASC, "time")), new Long[]{3L, 5L, 1L}),
                    arguments("thIS", PageRequest.of(0, 3, Sort.by(DESC, "time")), new Long[]{1L, 5L, 3L}),
                    arguments("THiS", PageRequest.of(0, 3, Sort.by(ASC, "username")), new Long[]{3L, 5L, 1L}),
                    arguments("ThIS", PageRequest.of(0, 3, Sort.by(DESC, "username")), new Long[]{1L, 3L, 5L})
            );
        }

        private static Stream<Arguments> provideCommentsTextAndPaginationParamsEmptyNews() {
            return Stream.of(
                    arguments("game", PageRequest.of(1, 2)),
                    arguments("GAME", PageRequest.of(2, 1)),
                    arguments("this", PageRequest.of(1, 3)),
                    arguments("THIS", PageRequest.of(2, 2)),
                    arguments("tHiS", PageRequest.of(3, 1)),
                    arguments("record", PageRequest.of(1, 1))
            );
        }
    }

    @Nested
    class NewsRepositoryFindAllByCommentsUsernameContainingIgnoreCaseTest {

        @ParameterizedTest
        @MethodSource("provideCommentsUsernameAndExpectedSizes")
        void checkFindAllByCommentsUsernameContainingIgnoreCaseShouldReturnSize(String commentsUsername, int expectedSize) {
            Pageable pageable = Pageable.unpaged();

            List<News> actualNews = newsRepository.findAllByCommentsUsernameContainingIgnoreCase(commentsUsername, pageable);

            assertThat(actualNews).hasSize(expectedSize);
        }

        @ParameterizedTest
        @MethodSource("provideCommentsUsernameAndNewsIds")
        void checkFindAllByCommentsUsernameContainingIgnoreShouldCaseReturnNewsIds(String commentsUsername, List<Long> newsIds) {
            Pageable pageable = Pageable.unpaged();

            List<News> actualNews = newsRepository.findAllByCommentsUsernameContainingIgnoreCase(commentsUsername, pageable);
            List<Long> actualNewsIds = actualNews.stream()
                    .map(News::getId)
                    .toList();

            assertThat(actualNewsIds).containsAll(newsIds);
        }

        @ParameterizedTest
        @MethodSource("provideCommentsUsernameAndPaginationParamsAndExpectedSizes")
        void checkFindAllByCommentsUsernameContainingIgnoreCaseShouldReturnSizeByPaginationParams(String commentsUsername,
                                                                                                  Pageable pageable,
                                                                                                  int expectedSize) {
            List<News> actualNews = newsRepository.findAllByCommentsUsernameContainingIgnoreCase(commentsUsername, pageable);

            assertThat(actualNews).hasSize(expectedSize);
        }

        @ParameterizedTest
        @MethodSource("provideCommentsUsernameAndSortingParamsAndNewsIds")
        void checkFindAllByCommentsUsernameContainingIgnoreCaseShouldReturnCommentIdsBySortingParams(String commentsUsername,
                                                                                                     Pageable pageable,
                                                                                                     Long[] newsIds) {
            List<News> actualNews = newsRepository.findAllByCommentsUsernameContainingIgnoreCase(commentsUsername, pageable);
            List<Long> actualNewsIds = actualNews.stream()
                    .map(News::getId)
                    .toList();

            assertThat(actualNewsIds).containsExactly(newsIds);
        }

        @ParameterizedTest
        @MethodSource("provideCommentsUsernameAndPaginationParamsEmptyNews")
        void checkFindAllByCommentsUsernameContainingIgnoreCaseShouldReturnEmptyNewsByPaginationParams(String commentsUsername,
                                                                                                       Pageable pageable) {
            List<News> actualNews = newsRepository.findAllByCommentsUsernameContainingIgnoreCase(commentsUsername, pageable);

            assertThat(actualNews).isEmpty();
        }

        @Test
        void checkFindAllByCommentsTextContainingIgnoreCaseShouldReturnEmptyNews() {
            String commentsUsername = "alex";
            Pageable pageable = Pageable.unpaged();

            List<News> actualNews = newsRepository.findAllByCommentsUsernameContainingIgnoreCase(commentsUsername, pageable);

            assertThat(actualNews).isEmpty();
        }

        private static Stream<Arguments> provideCommentsUsernameAndExpectedSizes() {
            return Stream.of(
                    arguments("debbie_garcia", 3),
                    arguments("GARCIA", 3),
                    arguments("ronnie_stevens", 4),
                    arguments("rOnNiE", 4),
                    arguments("tammy_simmons", 2),
                    arguments("SimMonS", 2)
            );
        }

        private static Stream<Arguments> provideCommentsUsernameAndNewsIds() {
            return Stream.of(
                    arguments("debbie_garcia", List.of(1L, 3L, 4L)),
                    arguments("GARCIA", List.of(1L, 3L, 4L)),
                    arguments("ronnie_stevens", List.of(1L, 2L, 3L, 4L)),
                    arguments("rOnNiE", List.of(1L, 2L, 3L, 4L)),
                    arguments("tammy_simmons",  List.of(3L, 5L)),
                    arguments("SimMonS", List.of(3L, 5L))
            );
        }

        private static Stream<Arguments> provideCommentsUsernameAndPaginationParamsAndExpectedSizes() {
            return Stream.of(
                    arguments("debbie_garcia", PageRequest.of(0, 1), 1),
                    arguments("GARCIA", PageRequest.of(0, 2), 2),
                    arguments("DeBbIe", PageRequest.of(0, 3), 3),
                    arguments("bie_gar", PageRequest.of(1, 1), 1),
                    arguments("Deb", PageRequest.of(2, 1), 1),
                    arguments("ronnie_stevens", PageRequest.of(0, 1), 1),
                    arguments("rOnNiE", PageRequest.of(0, 2), 2),
                    arguments("RONNIE", PageRequest.of(0, 3), 3),
                    arguments("roNNie_st", PageRequest.of(0, 4), 4),
                    arguments("ron", PageRequest.of(1, 1), 1),
                    arguments("StEVeNs", PageRequest.of(2, 1), 1),
                    arguments("STEv", PageRequest.of(3, 1), 1),
                    arguments("tammy_simmons", PageRequest.of(0, 1), 1),
                    arguments("SimMonS", PageRequest.of(0, 2), 2),
                    arguments("TAMMY", PageRequest.of(1, 1), 1)
            );
        }

        private static Stream<Arguments> provideCommentsUsernameAndSortingParamsAndNewsIds() {
            return Stream.of(
                    arguments("debbie_garcia", PageRequest.of(0, 3, Sort.by(ASC, "id")), new Long[]{1L, 3L, 4L}),
                    arguments("GARCIA", PageRequest.of(0, 3, Sort.by(DESC, "id")), new Long[]{4L, 3L, 1L}),
                    arguments("DeBbIe", PageRequest.of(0, 3, Sort.by(ASC, "title")), new Long[]{3L, 1L, 4L}),
                    arguments("bie_gar", PageRequest.of(0, 3, Sort.by(DESC, "title")), new Long[]{4L, 1L, 3L}),
                    arguments("Deb", PageRequest.of(0, 3, Sort.by(ASC, "text")), new Long[]{3L, 4L, 1L}),
                    arguments("GAR", PageRequest.of(0, 3, Sort.by(DESC, "text")), new Long[]{1L, 4L, 3L}),
                    arguments("gaRC", PageRequest.of(0, 3, Sort.by(ASC, "time")), new Long[]{3L, 4L, 1L}),
                    arguments("DEbb", PageRequest.of(0, 3, Sort.by(DESC, "time")), new Long[]{1L, 4L, 3L}),
                    arguments("bie_garcia", PageRequest.of(0, 3, Sort.by(ASC, "username")), new Long[]{3L, 1L, 4L}),
                    arguments("CIA", PageRequest.of(0, 3, Sort.by(DESC, "username")), new Long[]{1L, 4L, 3L}),
                    arguments("ronnie_stevens", PageRequest.of(0, 4, Sort.by(ASC, "id")), new Long[]{1L, 2L, 3L, 4L}),
                    arguments("RONNIE", PageRequest.of(0, 4, Sort.by(DESC, "id")), new Long[]{4L, 3L, 2L, 1L}),
                    arguments("STEVENS", PageRequest.of(0, 4, Sort.by(ASC, "title")), new Long[]{2L, 3L, 1L, 4L}),
                    arguments("ronnie_st", PageRequest.of(0, 4, Sort.by(DESC, "title")), new Long[]{4L, 1L, 3L, 2L}),
                    arguments("STeV", PageRequest.of(0, 4, Sort.by(ASC, "text")), new Long[]{3L, 4L, 1L, 2L}),
                    arguments("roN", PageRequest.of(0, 4, Sort.by(DESC, "text")), new Long[]{2L, 1L, 4L, 3L}),
                    arguments("nie_st", PageRequest.of(0, 4, Sort.by(ASC, "time")), new Long[]{3L, 4L, 1L, 2L}),
                    arguments("STeVen", PageRequest.of(0, 4, Sort.by(DESC, "time")), new Long[]{2L, 1L, 4L, 3L}),
                    arguments("VEns", PageRequest.of(0, 4, Sort.by(ASC, "username")), new Long[]{3L, 2L, 1L, 4L}),
                    arguments("nie", PageRequest.of(0, 4, Sort.by(DESC, "username")), new Long[]{1L, 4L, 2L, 3L})
            );
        }

        private static Stream<Arguments> provideCommentsUsernameAndPaginationParamsEmptyNews() {
            return Stream.of(
                    arguments("debbie_garcia", PageRequest.of(1, 3)),
                    arguments("GARCIA", PageRequest.of(2, 2)),
                    arguments("DeBbIe", PageRequest.of(3, 1)),
                    arguments("ronnie_stevens", PageRequest.of(1, 4)),
                    arguments("rOnNiE", PageRequest.of(2, 3)),
                    arguments("ron", PageRequest.of(3, 2)),
                    arguments("roNNie_st", PageRequest.of(4, 1))
            );
        }
    }
}
