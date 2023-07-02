package ru.clevertec.newsservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.dto.request.news.NewsTextRequest;
import ru.clevertec.newsservice.dto.response.user.UserResponse;
import ru.clevertec.newsservice.integration.BaseIntegrationTest;
import ru.clevertec.newsservice.util.request.NewsRequestTestBuilder;
import ru.clevertec.newsservice.util.request.NewsTextRequestTestBuilder;
import ru.clevertec.newsservice.util.response.UserResponseTestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class NewsControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class NewsControllerGetAllTest {

        @Test
        void checkGetAllShouldReturnStatusOkAndSize5() throws Exception {
            int expectedSize = 5;

            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("provideFilterParamsAndExpectedSizes")
        void checkGetAllShouldReturnStatusOkAndNewsResponsesSizeByFilterParams(String title,
                                                                               String text,
                                                                               String username,
                                                                               int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("title", title)
                            .param("text", text)
                            .param("username", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("providePaginationParamsAndExpectedSizes")
        void checkGetAllShouldReturnStatusOkAndNewsResponsesSizeByPaginationParam(String page,
                                                                                  String size,
                                                                                  int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("providePaginationParamsWithExpectedEmptySizes")
        void checkGetAllShouldReturnStatusOkAndEmptyNewsResponsesByPaginationParam(String page,
                                                                                   String size) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys()").isEmpty());
        }

        @ParameterizedTest
        @MethodSource("provideFilterAndPaginationParamsAndExpectedNotEmptySizes")
        void checkGetAllShouldReturnStatusOkAndNewsResponsesByFilterAndPaginationParams(String title,
                                                                                        String text,
                                                                                        String username,
                                                                                        String page,
                                                                                        String size,
                                                                                        int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("title", title)
                            .param("text", text)
                            .param("username", username)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        private static Stream<Arguments> provideFilterParamsAndExpectedSizes() {
            return Stream.of(
                    arguments("music", null, null, 1),
                    arguments("stream", null, null, 1),
                    arguments("great", null, null, 1),
                    arguments(null, "the", null, 3),
                    arguments(null, "for", null, 4),
                    arguments(null, "major", null, 2),
                    arguments(null, null, "thomas_martinez", 2),
                    arguments(null, null, "sharon_hill", 1),
                    arguments(null, null, "laura_norton", 2),
                    arguments(null, "the", "thomas", 1),
                    arguments("new", "for", null, 1),
                    arguments("music", "for", "norton", 1)
            );
        }

        private static Stream<Arguments> providePaginationParamsAndExpectedSizes() {
            return Stream.of(
                    arguments("0", "1", 1),
                    arguments("0", "2", 2),
                    arguments("0", "3", 3),
                    arguments("0", "4", 4),
                    arguments("0", "5", 5),
                    arguments("1", "1", 1),
                    arguments("1", "2", 2),
                    arguments("2", "1", 1),
                    arguments("3", "1", 1),
                    arguments("4", "1", 1)
            );
        }

        private static Stream<Arguments> providePaginationParamsWithExpectedEmptySizes() {
            return Stream.of(
                    arguments("0", "6"),
                    arguments("1", "5"),
                    arguments("2", "4"),
                    arguments("3", "3"),
                    arguments("4", "2"),
                    arguments("5", "1")
            );
        }

        private static Stream<Arguments> provideFilterAndPaginationParamsAndExpectedNotEmptySizes() {
            return Stream.of(
                    arguments("music", null, null, "0", "1", 1),
                    arguments("stream", null, null, "0", "1", 1),
                    arguments("great", null, null, "0", "1", 1),
                    arguments(null, "the", null, "0", "1", 1),
                    arguments(null, "the", null, "0", "2", 2),
                    arguments(null, "the", null, "0", "3", 3),
                    arguments(null, "the", null, "1", "1", 1),
                    arguments(null, "the", null, "2", "1", 1),
                    arguments(null, "for", null, "0", "1", 1),
                    arguments(null, "for", null, "0", "2", 2),
                    arguments(null, "for", null, "0", "3", 3),
                    arguments(null, "for", null, "0", "4", 4),
                    arguments(null, "for", null, "1", "1", 1),
                    arguments(null, "for", null, "2", "1", 1),
                    arguments(null, "for", null, "3", "1", 1),
                    arguments(null, "major", null, "0", "1", 1),
                    arguments(null, "major", null, "0", "2", 2),
                    arguments(null, "major", null, "1", "1", 1),
                    arguments(null, null, "thomas_martinez", "0", "1", 1),
                    arguments(null, null, "thomas_martinez", "0", "2", 2),
                    arguments(null, null, "thomas_martinez", "1", "1", 1),
                    arguments(null, null, "laura_norton", "0", "1", 1),
                    arguments(null, null, "laura_norton", "0", "2", 2),
                    arguments(null, null, "laura_norton", "1", "1", 1),
                    arguments(null, null, "sharon_hill", "0", "1", 1),
                    arguments(null, "the", "thomas", "0", "1", 1),
                    arguments("new", "for", null, "0", "1", 1),
                    arguments("music", "for", "norton", "0", "1", 1)
            );
        }
    }

    @Nested
    class NewsControllerGetAllByCommentsTextTest {

        @ParameterizedTest
        @MethodSource("provideCommentsTextAndExpectedSizes")
        void checkGetAllByCommentsTextShouldReturnStatusOkAndNewsResponsesSizeByCommentText(String commentText,
                                                                                            int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("commentText", commentText))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsTextAndPaginationParamsAndExpectedSizes")
        void checkGetAllByCommentsTextShouldReturnStatusOkAndNewsResponsesSizeByCommentTextAndPaginationParams(String commentText,
                                                                                                               String page,
                                                                                                               String size,
                                                                                                               int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("commentText", commentText)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsTextAndPaginationParamsEmptyNews")
        void checkGetAllByCommentsTextShouldReturnStatusOkAndEmptyNewsResponsesByCommentTextAndPaginationParams(String commentText,
                                                                                                                String page,
                                                                                                                String size) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("commentText", commentText)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys()").isEmpty());
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

        private static Stream<Arguments> provideCommentsTextAndPaginationParamsAndExpectedSizes() {
            return Stream.of(
                    arguments("game", "0", "1", 1),
                    arguments("GAME", "0", "2", 2),
                    arguments("gAmE", "1", "1", 1),
                    arguments("this", "0", "1", 1),
                    arguments("THIS", "0", "2", 2),
                    arguments("tHiS", "0", "3", 3),
                    arguments("ThIs", "1", "1", 1),
                    arguments("THiS", "2", "1", 1),
                    arguments("great", "0", "1", 1),
                    arguments("GREAT", "0", "2", 2),
                    arguments("gReAt", "1", "1", 1)
            );
        }

        private static Stream<Arguments> provideCommentsTextAndPaginationParamsEmptyNews() {
            return Stream.of(
                    arguments("game", "1", "2"),
                    arguments("GAME", "2", "1"),
                    arguments("this", "1", "3"),
                    arguments("THIS", "2", "2"),
                    arguments("tHiS", "3", "1"),
                    arguments("record", "1", "1")
            );
        }
    }

    @Nested
    class NewsControllerGetAllByCommentsUsernameTest {

        @ParameterizedTest
        @MethodSource("provideCommentsUsernameAndExpectedSizes")
        void checkGetAllByCommentsUsernameShouldReturnStatusOkAndNewsResponsesSizeByCommentUsername(String commentUsername,
                                                                                                    int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("commentUsername", commentUsername))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsUsernameAndPaginationParamsAndExpectedSizes")
        void checkGetAllByCommentUsernameShouldReturnStatusOkAndNewsResponsesSizeByCommentUsernameAndPaginationParams(String commentUsername,
                                                                                                                      String page,
                                                                                                                      String size,
                                                                                                                      int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("commentUsername", commentUsername)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsUsernameAndPaginationParamsEmptyNews")
        void checkGetAllByCommentsUsernameShouldReturnStatusOkAndEmptyNewsResponsesByCommentUsernameAndPaginationParams(String commentUsername,
                                                                                                                        String page,
                                                                                                                        String size) throws Exception {
            mockMvc.perform(get("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("commentUsername", commentUsername)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys()").isEmpty());
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

        private static Stream<Arguments> provideCommentsUsernameAndPaginationParamsAndExpectedSizes() {
            return Stream.of(
                    arguments("debbie_garcia", "0", "1", 1),
                    arguments("GARCIA", "0", "2", 2),
                    arguments("DeBbIe", "0", "3", 3),
                    arguments("bie_gar", "1", "1", 1),
                    arguments("Deb", "2", "1", 1),
                    arguments("ronnie_stevens", "0", "1", 1),
                    arguments("rOnNiE", "0", "2", 2),
                    arguments("RONNIE", "0", "3", 3),
                    arguments("roNNie_st", "0", "4", 4),
                    arguments("ron", "1", "1", 1),
                    arguments("StEVeNs", "2", "1", 1),
                    arguments("STEv", "3", "1", 1),
                    arguments("tammy_simmons", "0", "1", 1),
                    arguments("SimMonS", "0", "2", 2),
                    arguments("TAMMY", "1", "1", 1)
            );
        }

        private static Stream<Arguments> provideCommentsUsernameAndPaginationParamsEmptyNews() {
            return Stream.of(
                    arguments("debbie_garcia", "1", "3"),
                    arguments("GARCIA", "2", "2"),
                    arguments("DeBbIe", "3", "1"),
                    arguments("ronnie_stevens", "1", "4"),
                    arguments("rOnNiE", "2", "3"),
                    arguments("ron", "3", "2"),
                    arguments("roNNie_st", "4", "1")
            );
        }
    }

    @Nested
    class NewsControllerGetByIdTest {

        @ParameterizedTest
        @MethodSource("provideNewsIds")
        void checkGetByIdShouldReturnStatusOkAndCommentNewsResponse(Long id) throws Exception {
            mockMvc.perform(get("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotBlank());
        }

        @ParameterizedTest
        @MethodSource("provideNotFoundNewsIds")
        void checkGetByIdShouldReturnStatusNotFoundAndMessageNewsNotFound(Long id) throws Exception {
            String expectedMessage = "News with id = [%d] not found".formatted(id);

            mockMvc.perform(get("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Long> provideNewsIds() {
            return Stream.of(1L, 2L, 3L, 4L, 5L);
        }

        private static Stream<Long> provideNotFoundNewsIds() {
            return Stream.of(-1L, 0L, 100L, 1000L);
        }
    }

    @Nested
    class NewsControllerGetAllCommentsByIdTest {

        @ParameterizedTest
        @MethodSource("provideNewsIdsAndExpectedSizes")
        void checkGetAllCommentsByIdShouldReturnStatusOkAndSize(Long id, int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/news/{id}/comments", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("provideNewsIdsWithEmptyComments")
        void checkGetAllCommentsByIdShouldReturnStatusOkAndEmptyComments(Long id) throws Exception {
            mockMvc.perform(get("/api/v1/news/{id}/comments", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys()").isEmpty());
        }

        private static Stream<Arguments> provideNewsIdsAndExpectedSizes() {
            return Stream.of(
                    arguments(1L, 2),
                    arguments(2L, 1),
                    arguments(3L, 3),
                    arguments(4L, 2),
                    arguments(5L, 2)
            );
        }

        private static Stream<Long> provideNewsIdsWithEmptyComments() {
            return Stream.of(-1L, 0L, 100L, 1000L);
        }
    }

    @Nested
    class NewsControllerGetCommentByIdTest {

        @ParameterizedTest
        @MethodSource("provideCommentIdAndNewsIdComments")
        void checkGetCommentByIdShouldReturnStatusOkAndCommentResponse(Long commentId, Long newsId) throws Exception {
            mockMvc.perform(get("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotBlank());
        }

        @ParameterizedTest
        @MethodSource("provideCommentIdAndNewsIdNotPresentComments")
        void checkGetCommentByIdShouldReturnStatusNotFoundAndMessageNewsNotFound(Long commentId, Long newsId) throws Exception {
            String expectedMessage = "Comment with id = [%d] news with id = [%d] not found".formatted(commentId, newsId);

            mockMvc.perform(get("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
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

    @Nested
    @WireMockTest(httpPort = 8081)
    class NewsControllerSaveTest {

        @ParameterizedTest
        @MethodSource("provideAccessibleUsernameAndAuthorities")
        void checkSaveShouldReturnStatusCreatedAndNewsCommentResponse(String username,
                                                                      List<String> authorities) throws Exception {
            NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                    .withTitle("News title")
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername(username)
                    .withAuthorities(authorities)
                    .build();
            String newsRequestBody = objectMapper.writeValueAsString(newsRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(post("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsRequestBody))
                    .andExpect(status().isCreated())
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotBlank());
        }

        @ParameterizedTest
        @MethodSource("provideNotValidNewsRequestsAndExpectedMessages")
        void checkSaveShouldReturnStatusBadRequestAndMessageBadRequest(NewsRequest newsRequest,
                                                                       String expectedMessage) throws Exception {
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String newsRequestBody = objectMapper.writeValueAsString(newsRequest);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(post("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsRequestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value(expectedMessage));
        }

        @Test
        void checkSaveShouldReturnStatusForbiddenAndMessageAccessDenied() throws Exception {
            NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                    .withTitle("News title")
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String newsRequestBody = objectMapper.writeValueAsString(newsRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Access Denied";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(post("/api/v1/news")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsRequestBody))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Arguments> provideAccessibleUsernameAndAuthorities() {
            return Stream.of(
                    arguments("erik_gibson", Collections.singletonList("ROLE_ADMIN")),
                    arguments("thomas_martinez", Collections.singletonList("ROLE_JOURNALIST"))
            );
        }

        private static Stream<Arguments> provideNotValidNewsRequestsAndExpectedMessages() {
            return Stream.of(
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle(null).withText("Good news").build(),
                            "News title cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle("").withText("Good news").build(),
                            "News title cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle(" ").withText("Good news").build(),
                            "News title cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle("News title").withText(null).build(),
                            "News text cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle("News title").withText("").build(),
                            "News text cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle("News title").withText(" ").build(),
                            "News text cannot be empty"
                    )
            );
        }
    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class NewsControllerUpdateTest {

        @ParameterizedTest
        @MethodSource("provideAccessibleUsernameAndAuthorities")
        void checkUpdateShouldReturnStatusOkAndCommentNewsResponse(String username,
                                                                   List<String> authorities) throws Exception {
            Long id = 1L;
            NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                    .withTitle("News title")
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername(username)
                    .withAuthorities(authorities)
                    .build();
            String newsRequestBody = objectMapper.writeValueAsString(newsRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsRequestBody))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotBlank());
        }

        @ParameterizedTest
        @MethodSource("provideNotFoundNewsIds")
        void checkUpdateShouldReturnStatusNotFoundAndMessageNewsNotFound(Long id) throws Exception {
            NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                    .withTitle("News title")
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String newsRequestBody = objectMapper.writeValueAsString(newsRequest);
            String expectedMessage = "News with id = [%d] not found".formatted(id);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsRequestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideNotValidNewsRequestsAndExpectedMessages")
        void checkSaveShouldReturnStatusBadRequestAndMessageEmptyNewsFields(NewsRequest newsRequest,
                                                                            String expectedMessage) throws Exception {
            Long id = 1L;
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String newsRequestBody = objectMapper.writeValueAsString(newsRequest);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsRequestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value(expectedMessage));
        }

        @Test
        void checkUpdateShouldReturnStatusForbiddenAndMessageAccessDeniedForSubscriber() throws Exception {
            Long id = 1L;
            NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                    .withTitle("News title")
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String newsRequestBody = objectMapper.writeValueAsString(newsRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Access Denied";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsRequestBody))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsIdsOfOtherJournalists")
        void checkUpdateShouldReturnStatusForbiddenAndMessageAccessDeniedForNewsOfOtherJournalists(Long id) throws Exception {
            NewsRequest newsRequest = NewsRequestTestBuilder.newsRequest()
                    .withTitle("News title")
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String newsRequestBody = objectMapper.writeValueAsString(newsRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Journalist can only update his news";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsRequestBody))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Arguments> provideAccessibleUsernameAndAuthorities() {
            return Stream.of(
                    arguments("erik_gibson", Collections.singletonList("ROLE_ADMIN")),
                    arguments("thomas_martinez", Collections.singletonList("ROLE_JOURNALIST"))
            );
        }

        private static Stream<Long> provideNotFoundNewsIds() {
            return Stream.of(-1L, 0L, 50L, 100L, 1000L);
        }

        private static Stream<Arguments> provideNotValidNewsRequestsAndExpectedMessages() {
            return Stream.of(
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle(null).withText("Good news").build(),
                            "News title cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle("").withText("Good news").build(),
                            "News title cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle(" ").withText("Good news").build(),
                            "News title cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle("News title").withText(null).build(),
                            "News text cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle("News title").withText("").build(),
                            "News text cannot be empty"
                    ),
                    arguments(
                            NewsRequestTestBuilder.newsRequest().withTitle("News title").withText(" ").build(),
                            "News text cannot be empty"
                    )
            );
        }

        private static Stream<Long> provideCommentsIdsOfOtherJournalists() {
            return Stream.of(2L, 3L, 5L);
        }
    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class NewsControllerUpdateTextTest {

        @ParameterizedTest
        @MethodSource("provideAccessibleUsernameAndAuthorities")
        void checkUpdateShouldReturnStatusOkAndCommentNewsResponse(String username,
                                                                   List<String> authorities) throws Exception {
            Long id = 1L;
            NewsTextRequest newsTextRequest = NewsTextRequestTestBuilder.newsTextRequest()
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername(username)
                    .withAuthorities(authorities)
                    .build();
            String newsTextRequestBody = objectMapper.writeValueAsString(newsTextRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(patch("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsTextRequestBody))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotBlank());
        }

        @ParameterizedTest
        @MethodSource("provideNotFoundNewsIds")
        void checkUpdateShouldReturnStatusNotFoundAndMessageNewsNotFound(Long id) throws Exception {
            NewsTextRequest newsTextRequest = NewsTextRequestTestBuilder.newsTextRequest()
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String newsTextRequestBody = objectMapper.writeValueAsString(newsTextRequest);
            String expectedMessage = "News with id = [%d] not found".formatted(id);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(patch("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsTextRequestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideNotValidNewsTextRequests")
        void checkSaveShouldReturnStatusBadRequestAndMessageEmptyNewsText(NewsTextRequest newsTextRequest) throws Exception {
            Long id = 1L;
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String newsTextRequestBody = objectMapper.writeValueAsString(newsTextRequest);
            String expectedMessage = "News text cannot be empty";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(patch("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsTextRequestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value(expectedMessage));
        }

        @Test
        void checkUpdateShouldReturnStatusForbiddenAndMessageAccessDeniedForSubscriber() throws Exception {
            Long id = 1L;
            NewsTextRequest newsTextRequest = NewsTextRequestTestBuilder.newsTextRequest()
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String newsTextRequestBody = objectMapper.writeValueAsString(newsTextRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Access Denied";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(patch("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsTextRequestBody))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsIdsOfOtherJournalists")
        void checkUpdateShouldReturnStatusForbiddenAndMessageAccessDeniedForNewsOfOtherJournalists(Long id) throws Exception {
            NewsTextRequest newsTextRequest = NewsTextRequestTestBuilder.newsTextRequest()
                    .withText("Good news")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String newsTextRequestBody = objectMapper.writeValueAsString(newsTextRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Journalist can only update his news text";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(patch("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsTextRequestBody))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Arguments> provideAccessibleUsernameAndAuthorities() {
            return Stream.of(
                    arguments("erik_gibson", Collections.singletonList("ROLE_ADMIN")),
                    arguments("thomas_martinez", Collections.singletonList("ROLE_JOURNALIST"))
            );
        }

        private static Stream<Long> provideNotFoundNewsIds() {
            return Stream.of(-1L, 0L, 50L, 100L, 1000L);
        }

        private static Stream<NewsTextRequest> provideNotValidNewsTextRequests() {
            return Stream.of(
                    NewsTextRequestTestBuilder.newsTextRequest().withText(null).build(),
                    NewsTextRequestTestBuilder.newsTextRequest().withText("").build(),
                    NewsTextRequestTestBuilder.newsTextRequest().withText(" ").build()
            );
        }

        private static Stream<Long> provideCommentsIdsOfOtherJournalists() {
            return Stream.of(2L, 3L, 5L);
        }
    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class NewsControllerDeleteByIdTest {

        @ParameterizedTest
        @MethodSource("provideAccessibleUsernameAndAuthorities")
        void checkDeleteByIdShouldReturnStatusOk(String username,
                                                 List<String> authorities) throws Exception {
            Long id = 1L;
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername(username)
                    .withAuthorities(authorities)
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(delete("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @ParameterizedTest
        @MethodSource("provideCommentsNotFoundIds")
        void checkDeleteByIdShouldReturnStatusNotFoundAndMessageCommentNotFound(Long id) throws Exception {
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "News with id = [%d] not found".formatted(id);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(delete("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @Test
        void checkDeleteByIdShouldReturnStatusForbiddenAndMessageAccessDeniedForSubscriber() throws Exception {
            Long id = 1L;
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Access Denied";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(delete("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsIdsOfOtherJournalists")
        void checkUpdateShouldReturnStatusForbiddenAndMessageAccessDeniedForCommentsOfOtherJournalists(Long id) throws Exception {
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Journalist can only delete his news";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(delete("/api/v1/news/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Arguments> provideAccessibleUsernameAndAuthorities() {
            return Stream.of(
                    arguments("erik_gibson", Collections.singletonList("ROLE_ADMIN")),
                    arguments("thomas_martinez", Collections.singletonList("ROLE_JOURNALIST"))
            );
        }

        private static Stream<Long> provideCommentsNotFoundIds() {
            return Stream.of(-1L, 0L, 50L, 100L, 1000L);
        }

        private static Stream<Long> provideCommentsIdsOfOtherJournalists() {
            return Stream.of(2L, 3L, 5L);
        }
    }
}
