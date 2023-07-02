package ru.clevertec.newsservice.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.clevertec.loggingstarter.annotation.Loggable;
import ru.clevertec.newsservice.dto.filter.CommentFilter;
import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;
import ru.clevertec.newsservice.service.impl.CommentServiceImpl;

import java.security.Principal;
import java.util.List;

@Loggable
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comments")
public class CommentController {

    private final CommentServiceImpl commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAll(CommentFilter commentFilter, Pageable pageable) {
        List<CommentResponse> commentResponses = commentService.findAll(commentFilter, pageable);
        return ResponseEntity.ok(commentResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<NewsCommentResponse> getById(@PathVariable Long id) {
        NewsCommentResponse newsCommentResponse = commentService.findById(id);
        return ResponseEntity.ok(newsCommentResponse);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBSCRIBER')")
    public ResponseEntity<NewsCommentResponse> save(@RequestBody @Valid CommentRequest commentRequest,
                                                    Principal principal) {
        NewsCommentResponse newsCommentResponse = commentService.save(commentRequest, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(newsCommentResponse);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBSCRIBER')")
    public ResponseEntity<NewsCommentResponse> update(@PathVariable Long id,
                                                      @RequestBody @Valid NewsCommentRequest newsCommentRequest) {
        NewsCommentResponse newsCommentResponse = commentService.update(id, newsCommentRequest);
        return ResponseEntity.ok(newsCommentResponse);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUBSCRIBER')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        commentService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
