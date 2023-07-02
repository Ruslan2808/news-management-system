package ru.clevertec.newsservice.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.clevertec.loggingstarter.annotation.Loggable;
import ru.clevertec.newsservice.dto.filter.NewsFilter;
import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.dto.request.news.NewsTextRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.service.impl.CommentServiceImpl;
import ru.clevertec.newsservice.service.impl.NewsServiceImpl;

import java.security.Principal;
import java.util.List;

@Loggable
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/news")
public class NewsController {

    private final NewsServiceImpl newsService;
    private final CommentServiceImpl commentService;

    @GetMapping
    public ResponseEntity<List<NewsResponse>> getAll(NewsFilter newsFilter, Pageable pageable) {
        List<NewsResponse> newsResponses = newsService.findAll(newsFilter, pageable);
        return ResponseEntity.ok(newsResponses);
    }

    @GetMapping(params = "commentText")
    public ResponseEntity<List<NewsResponse>> getAllByCommentsText(String commentText, Pageable pageable) {
        List<NewsResponse> newsResponses = newsService.findAllByCommentsText(commentText, pageable);
        return ResponseEntity.ok(newsResponses);
    }

    @GetMapping(params = "commentUsername")
    public ResponseEntity<List<NewsResponse>> getAllByCommentsUsername(String commentUsername, Pageable pageable) {
        List<NewsResponse> newsResponses = newsService.findAllByCommentsUsername(commentUsername, pageable);
        return ResponseEntity.ok(newsResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<CommentNewsResponse> getById(@PathVariable Long id) {
        CommentNewsResponse commentNewsResponse = newsService.findById(id);
        return ResponseEntity.ok(commentNewsResponse);
    }

    @GetMapping("{id}/comments")
    public ResponseEntity<List<CommentResponse>> getAllCommentsById(@PathVariable Long id, Pageable pageable) {
        List<CommentResponse> commentResponses = commentService.findAllByNewsId(id, pageable);
        return ResponseEntity.ok(commentResponses);
    }

    @GetMapping("{newsId}/comments/{commentId}")
    public ResponseEntity<NewsCommentResponse> getCommentById(@PathVariable Long newsId,
                                                              @PathVariable Long commentId) {
        NewsCommentResponse newsCommentResponse = commentService.findByIdAndNewsId(commentId, newsId);
        return ResponseEntity.ok(newsCommentResponse);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'JOURNALIST')")
    public ResponseEntity<CommentNewsResponse> save(@RequestBody @Valid NewsRequest newsRequest,
                                                    Principal principal) {
        CommentNewsResponse commentNewsResponse = newsService.save(newsRequest, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentNewsResponse);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JOURNALIST')")
    public ResponseEntity<CommentNewsResponse> update(@PathVariable Long id,
                                                      @RequestBody @Valid NewsRequest newsRequest) {
        CommentNewsResponse commentNewsResponse = newsService.update(id, newsRequest);
        return ResponseEntity.ok(commentNewsResponse);
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JOURNALIST')")
    public ResponseEntity<CommentNewsResponse> updateText(@PathVariable Long id,
                                                          @RequestBody @Valid NewsTextRequest newsTextRequest) {
        CommentNewsResponse commentNewsResponse = newsService.updateText(id, newsTextRequest);
        return ResponseEntity.ok(commentNewsResponse);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JOURNALIST')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        newsService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
