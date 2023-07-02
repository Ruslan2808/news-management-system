package ru.clevertec.newsservice.service;

import org.springframework.data.domain.Pageable;

import ru.clevertec.newsservice.dto.filter.NewsFilter;
import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.dto.request.news.NewsTextRequest;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;

import java.security.Principal;
import java.util.List;

public interface NewsService {

    List<NewsResponse> findAll(NewsFilter newsFilter, Pageable pageable);
    List<NewsResponse> findAllByCommentsText(String commentText, Pageable pageable);
    List<NewsResponse> findAllByCommentsUsername(String commentUsername, Pageable pageable);
    CommentNewsResponse findById(Long id);
    CommentNewsResponse save(NewsRequest newsRequest, Principal principal);
    CommentNewsResponse update(Long id, NewsRequest newsRequest);
    CommentNewsResponse updateText(Long id, NewsTextRequest newsTextRequest);
    void deleteById(Long id);

}
