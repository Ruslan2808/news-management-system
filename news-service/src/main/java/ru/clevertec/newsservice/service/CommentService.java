package ru.clevertec.newsservice.service;

import org.springframework.data.domain.Pageable;

import ru.clevertec.newsservice.dto.filter.CommentFilter;
import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;

import java.security.Principal;
import java.util.List;

public interface CommentService {

    List<CommentResponse> findAll(CommentFilter commentFilter, Pageable pageable);
    List<CommentResponse> findAllByNewsId(Long id, Pageable pageable);
    NewsCommentResponse findById(Long id);
    NewsCommentResponse findByIdAndNewsId(Long commentId, Long newsId);
    NewsCommentResponse save(CommentRequest commentRequest, Principal principal);
    NewsCommentResponse update(Long id, NewsCommentRequest newsCommentRequest);
    void deleteById(Long id);

}
