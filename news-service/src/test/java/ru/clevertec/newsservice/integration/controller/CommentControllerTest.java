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

import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.dto.response.user.UserResponse;
import ru.clevertec.newsservice.integration.BaseIntegrationTest;
import ru.clevertec.newsservice.util.request.CommentRequestTestBuilder;
import ru.clevertec.newsservice.util.request.NewsCommentRequestTestBuilder;
import ru.clevertec.newsservice.util.response.UserResponseTestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class CommentControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CommentControllerGetAllTest {

        @Test
        void checkGetAllShouldReturnStatusOkAndSize10() throws Exception {
            int expectedSize = 10;

            mockMvc.perform(get("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("provideFilterParamsAndExpectedSizes")
        void checkGetAllShouldReturnStatusOkAndCommentResponsesSizeByFilterParams(String text,
                                                                                  String username,
                                                                                  int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("text", text)
                            .param("username", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("providePaginationParamsAndExpectedSizes")
        void checkGetAllShouldReturnStatusOkAndCommentResponsesSizeByPaginationParam(String page,
                                                                                     String size,
                                                                                     int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        @ParameterizedTest
        @MethodSource("providePaginationParamsWithExpectedEmptySizes")
        void checkGetAllShouldReturnStatusOkAndEmptyCommentResponsesByPaginationParam(String page,
                                                                                      String size) throws Exception {
            mockMvc.perform(get("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys()").isEmpty());
        }

        @ParameterizedTest
        @MethodSource("provideFilterAndPaginationParamsAndExpectedNotEmptySizes")
        void checkGetAllShouldReturnStatusOkAndCommentResponsesByFilterAndPaginationParams(String text,
                                                                                           String username,
                                                                                           String page,
                                                                                           String size,
                                                                                           int expectedSize) throws Exception {
            mockMvc.perform(get("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .param("text", text)
                            .param("username", username)
                            .param("page", page)
                            .param("size", size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(expectedSize));
        }

        private static Stream<Arguments> provideFilterParamsAndExpectedSizes() {
            return Stream.of(
                    arguments("game", null, 2),
                    arguments("this", null, 3),
                    arguments("great", null, 3),
                    arguments("record", null, 1),
                    arguments("human", null, 1),
                    arguments(null, "debbie_garcia", 3),
                    arguments(null, "ronnie", 4),
                    arguments(null, "simmons", 3),
                    arguments("game", "ronnie_stevens", 1),
                    arguments("game", "debbie", 1),
                    arguments("this", "stevens", 1),
                    arguments("this", "garcia", 1),
                    arguments("this", "tammy_simmons", 1),
                    arguments("great", "debbie_garcia", 2),
                    arguments("great", "ron", 1),
                    arguments("record", "tammy", 1),
                    arguments("human", "simmons", 1)
            );
        }

        private static Stream<Arguments> providePaginationParamsAndExpectedSizes() {
            return Stream.of(
                    arguments("0", "1", 1),
                    arguments("0", "2", 2),
                    arguments("0", "3", 3),
                    arguments("0", "4", 4),
                    arguments("0", "5", 5),
                    arguments("0", "6", 6),
                    arguments("0", "7", 7),
                    arguments("0", "8", 8),
                    arguments("0", "9", 9),
                    arguments("0", "10", 10),
                    arguments("1", "1", 1),
                    arguments("1", "2", 2),
                    arguments("1", "3", 3),
                    arguments("1", "4", 4),
                    arguments("1", "5", 5),
                    arguments("2", "1", 1),
                    arguments("2", "2", 2),
                    arguments("2", "3", 3),
                    arguments("3", "1", 1),
                    arguments("3", "2", 2),
                    arguments("4", "1", 1),
                    arguments("4", "2", 2),
                    arguments("5", "1", 1),
                    arguments("6", "1", 1),
                    arguments("7", "1", 1),
                    arguments("8", "1", 1),
                    arguments("9", "1", 1)
            );
        }

        private static Stream<Arguments> providePaginationParamsWithExpectedEmptySizes() {
            return Stream.of(
                    arguments("0", "11"),
                    arguments("1", "10"),
                    arguments("2", "9"),
                    arguments("3", "8"),
                    arguments("4", "7"),
                    arguments("5", "6"),
                    arguments("6", "5"),
                    arguments("7", "4"),
                    arguments("8", "3"),
                    arguments("9", "2"),
                    arguments("10", "1")
            );
        }

        private static Stream<Arguments> provideFilterAndPaginationParamsAndExpectedNotEmptySizes() {
            return Stream.of(
                    arguments("game", null, "0", "1", 1),
                    arguments("game", null, "0", "2", 2),
                    arguments("game", null, "1", "1", 1),
                    arguments("this", null, "0", "1", 1),
                    arguments("this", null, "0", "2", 2),
                    arguments("this", null, "0", "3", 3),
                    arguments("this", null, "1", "1", 1),
                    arguments("this", null, "2", "1", 1),
                    arguments("great", null, "0", "1", 1),
                    arguments("great", null, "0", "2", 2),
                    arguments("great", null, "0", "3", 3),
                    arguments("great", null, "1", "1", 1),
                    arguments("great", null, "2", "1", 1),
                    arguments(null, "debbie_garcia", "0", "1", 1),
                    arguments(null, "debbie_garcia", "0", "2", 2),
                    arguments(null, "debbie_garcia", "0", "3", 3),
                    arguments(null, "debbie_garcia", "1", "1", 1),
                    arguments(null, "debbie_garcia", "2", "1", 1),
                    arguments(null, "ronnie", "0", "1", 1),
                    arguments(null, "ronnie", "0", "2", 2),
                    arguments(null, "ronnie", "0", "3", 3),
                    arguments(null, "ronnie", "1", "1", 1),
                    arguments(null, "ronnie", "2", "1", 1),
                    arguments(null, "ronnie", "3", "1", 1),
                    arguments("great", "debbie_garcia", "0", "1", 1),
                    arguments("great", "debbie_garcia", "0", "2", 2),
                    arguments("great", "debbie_garcia", "1", "1", 1)
            );
        }
    }

    @Nested
    class CommentControllerGetByIdTest {

        @ParameterizedTest
        @MethodSource("provideCommentIds")
        void checkGetByIdShouldReturnStatusOkAndNewsCommentResponse(Long id) throws Exception {
            mockMvc.perform(get("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotBlank());
        }

        @ParameterizedTest
        @MethodSource("provideNotFoundCommentIds")
        void checkGetByIdShouldReturnStatusNotFoundAndMessageCommentNotFound(Long id) throws Exception {
            String expectedMessage = "Comment with id = [%d] not found".formatted(id);

            mockMvc.perform(get("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Long> provideCommentIds() {
            return Stream.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        }

        private static Stream<Long> provideNotFoundCommentIds() {
            return Stream.of(-1L, 0L, 100L, 1000L);
        }
    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CommentControllerSaveTest {

        @ParameterizedTest
        @MethodSource("provideAccessibleUsernameAndAuthorities")
        void checkSaveShouldReturnStatusCreatedAndNewsCommentResponse(String username,
                                                                      List<String> authorities) throws Exception {
            CommentRequest commentRequest = CommentRequestTestBuilder.commentRequest()
                    .withText("Good comment")
                    .withNewsId(1L)
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername(username)
                    .withAuthorities(authorities)
                    .build();
            String commentRequestBody = objectMapper.writeValueAsString(commentRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(post("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(commentRequestBody))
                    .andExpect(status().isCreated())
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotBlank());
        }

        @ParameterizedTest
        @MethodSource("provideCommentRequestsWithNotFoundNewsIds")
        void checkSaveShouldReturnStatusNotFoundAndMessageNewsNotFound(CommentRequest commentRequest) throws Exception {
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String commentRequestBody = objectMapper.writeValueAsString(commentRequest);
            String expectedMessage = "News with id = [%d] not found".formatted(commentRequest.getNewsId());

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(post("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(commentRequestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideNotValidCommentRequestsAndExpectedMessages")
        void checkSaveShouldReturnStatusBadRequestAndMessageBadRequest(CommentRequest commentRequest,
                                                                       String expectedMessage) throws Exception {
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String commentRequestBody = objectMapper.writeValueAsString(commentRequest);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(post("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(commentRequestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value(expectedMessage));
        }

        @Test
        void checkSaveShouldReturnStatusForbiddenAndMessageAccessDenied() throws Exception {
            CommentRequest commentRequest = CommentRequestTestBuilder.commentRequest()
                    .withText("Good comment")
                    .withNewsId(1L)
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String commentRequestBody = objectMapper.writeValueAsString(commentRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Access Denied";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(post("/api/v1/comments")
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(commentRequestBody))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Arguments> provideAccessibleUsernameAndAuthorities() {
            return Stream.of(
                    arguments("erik_gibson", Collections.singletonList("ROLE_ADMIN")),
                    arguments("debbie_garcia", Collections.singletonList("ROLE_SUBSCRIBER"))
            );
        }

        private static Stream<CommentRequest> provideCommentRequestsWithNotFoundNewsIds() {
            return Stream.of(
                    CommentRequestTestBuilder.commentRequest().withText("Good comment").withNewsId(10L).build(),
                    CommentRequestTestBuilder.commentRequest().withText("Good comment").withNewsId(100L).build(),
                    CommentRequestTestBuilder.commentRequest().withText("Good comment").withNewsId(1000L).build()
            );
        }

        private static Stream<Arguments> provideNotValidCommentRequestsAndExpectedMessages() {
            return Stream.of(
                    arguments(
                            CommentRequestTestBuilder.commentRequest().withText(null).withNewsId(1L).build(),
                            "Comment text cannot be empty"
                    ),
                    arguments(
                            CommentRequestTestBuilder.commentRequest().withText("").withNewsId(1L).build(),
                            "Comment text cannot be empty"
                    ),
                    arguments(
                            CommentRequestTestBuilder.commentRequest().withText(" ").withNewsId(1L).build(),
                            "Comment text cannot be empty"
                    ),
                    arguments(
                            CommentRequestTestBuilder.commentRequest().withText("Good comment").withNewsId(null).build(),
                            "News id cannot be empty"
                    ),
                    arguments(
                            CommentRequestTestBuilder.commentRequest().withText("Good comment").withNewsId(-1L).build(),
                            "News id must be positive"
                    ),
                    arguments(
                            CommentRequestTestBuilder.commentRequest().withText("Good comment").withNewsId(0L).build(),
                            "News id must be positive"
                    )
            );
        }
    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CommentControllerUpdateTest {

        @ParameterizedTest
        @MethodSource("provideAccessibleUsernameAndAuthorities")
        void checkUpdateShouldReturnStatusOkAndNewsCommentResponse(String username,
                                                                   List<String> authorities) throws Exception {
            Long id = 1L;
            NewsCommentRequest newsCommentRequest = NewsCommentRequestTestBuilder.newsCommentRequest()
                    .withText("Good comment")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername(username)
                    .withAuthorities(authorities)
                    .build();
            String newsCommentRequestBody = objectMapper.writeValueAsString(newsCommentRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsCommentRequestBody))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotBlank());
        }

        @ParameterizedTest
        @MethodSource("provideNotFoundNewsIds")
        void checkUpdateShouldReturnStatusNotFoundAndMessageNewsNotFound(Long id) throws Exception {
            NewsCommentRequest newsCommentRequest = NewsCommentRequestTestBuilder.newsCommentRequest()
                    .withText("Good comment")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String newsCommentRequestBody = objectMapper.writeValueAsString(newsCommentRequest);
            String expectedMessage = "Comment with id = [%d] not found".formatted(id);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsCommentRequestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideNotValidNewsCommentRequests")
        void checkSaveShouldReturnStatusBadRequestAndMessageEmptyCommentText(NewsCommentRequest newsCommentRequest) throws Exception {
            Long id = 1L;
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String newsCommentRequestBody = objectMapper.writeValueAsString(newsCommentRequest);
            String expectedMessage = "Comment text cannot be empty";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsCommentRequestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value(expectedMessage));
        }

        @Test
        void checkUpdateShouldReturnStatusForbiddenAndMessageAccessDeniedForJournalist() throws Exception {
            Long id = 1L;
            NewsCommentRequest newsCommentRequest = NewsCommentRequestTestBuilder.newsCommentRequest()
                    .withText("Good comment")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String newsCommentRequestBody = objectMapper.writeValueAsString(newsCommentRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Access Denied";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsCommentRequestBody))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsIdsOfOtherSubscribers")
        void checkUpdateShouldReturnStatusForbiddenAndMessageAccessDeniedForCommentsOfOtherSubscribers(Long id) throws Exception {
            NewsCommentRequest newsCommentRequest = NewsCommentRequestTestBuilder.newsCommentRequest()
                    .withText("Good comment")
                    .build();
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String newsCommentRequestBody = objectMapper.writeValueAsString(newsCommentRequest);
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Subscriber can only update his comments";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(put("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(newsCommentRequestBody))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Arguments> provideAccessibleUsernameAndAuthorities() {
            return Stream.of(
                    arguments("erik_gibson", Collections.singletonList("ROLE_ADMIN")),
                    arguments("debbie_garcia", Collections.singletonList("ROLE_SUBSCRIBER"))
            );
        }

        private static Stream<Long> provideNotFoundNewsIds() {
            return Stream.of(-1L, 0L, 50L, 100L, 1000L);
        }

        private static Stream<NewsCommentRequest> provideNotValidNewsCommentRequests() {
            return Stream.of(
                    NewsCommentRequestTestBuilder.newsCommentRequest().withText(null).build(),
                    NewsCommentRequestTestBuilder.newsCommentRequest().withText("").build(),
                    NewsCommentRequestTestBuilder.newsCommentRequest().withText(" ").build()
            );
        }

        private static Stream<Long> provideCommentsIdsOfOtherSubscribers() {
            return Stream.of(2L, 3L, 5L, 6L, 8L, 9L, 10L);
        }
    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class CommentControllerDeleteByIdTest {

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

            mockMvc.perform(delete("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @ParameterizedTest
        @MethodSource("provideCommentsNotFoundIds")
        void checkDeleteByIdShouldReturnStatusNotFoundAndMessageCommentNotFound(Long id) throws Exception {
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Comment with id = [%d] not found".formatted(id);

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(delete("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @Test
        void checkDeleteByIdShouldReturnStatusForbiddenAndMessageAccessDeniedForJournalist() throws Exception {
            Long id = 1L;
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("thomas_martinez")
                    .withAuthorities(Collections.singletonList("ROLE_JOURNALIST"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Access Denied";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(delete("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        @ParameterizedTest
        @MethodSource("provideCommentsIdsOfOtherSubscribers")
        void checkUpdateShouldReturnStatusForbiddenAndMessageAccessDeniedForCommentsOfOtherSubscribers(Long id) throws Exception {
            UserResponse userResponse = UserResponseTestBuilder.userResponse()
                    .withUsername("debbie_garcia")
                    .withAuthorities(Collections.singletonList("ROLE_SUBSCRIBER"))
                    .build();
            String userResponseBody = objectMapper.writeValueAsString(userResponse);
            String expectedMessage = "Subscriber can only delete his comments";

            stubFor(WireMock.get(urlEqualTo("/api/v1/auth/validate"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(userResponseBody)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                    ));

            mockMvc.perform(delete("/api/v1/comments/{id}", id)
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        private static Stream<Arguments> provideAccessibleUsernameAndAuthorities() {
            return Stream.of(
                    arguments("erik_gibson", Collections.singletonList("ROLE_ADMIN")),
                    arguments("debbie_garcia", Collections.singletonList("ROLE_SUBSCRIBER"))
            );
        }

        private static Stream<Long> provideCommentsNotFoundIds() {
            return Stream.of(-1L, 0L, 50L, 100L, 1000L);
        }

        private static Stream<Long> provideCommentsIdsOfOtherSubscribers() {
            return Stream.of(2L, 3L, 5L, 6L, 8L, 9L, 10L);
        }
    }
}
