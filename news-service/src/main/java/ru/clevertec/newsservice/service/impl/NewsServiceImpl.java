package ru.clevertec.newsservice.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.clevertec.exceptionhandlingstarter.exception.NewsNotFoundException;
import ru.clevertec.newsservice.dto.filter.NewsFilter;
import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.dto.request.news.NewsTextRequest;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.mapper.NewsMapper;
import ru.clevertec.newsservice.repository.NewsRepository;
import ru.clevertec.newsservice.service.NewsService;

import java.security.Principal;
import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

import static ru.clevertec.newsservice.security.util.SecurityUtil.isAccessRole;
import static ru.clevertec.newsservice.security.util.SecurityUtil.isAccessUsername;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "newsCache")
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;
    private final NewsRepository newsRepository;

    @Override
    public List<NewsResponse> findAll(NewsFilter newsFilter, Pageable pageable) {
        ExampleMatcher newsMatcher = ExampleMatcher.matching()
                .withMatcher("title", contains().ignoreCase())
                .withMatcher("text", contains().ignoreCase())
                .withMatcher("username", contains().ignoreCase());

        News filteredNews = newsMapper.mapToNews(newsFilter);
        Example<News> newsExample = Example.of(filteredNews, newsMatcher);

        List<News> news = newsRepository.findAll(newsExample, pageable).getContent();

        return newsMapper.mapToNewsResponses(news);
    }

    @Override
    public List<NewsResponse> findAllByCommentsText(String commentText, Pageable pageable) {
        List<News> news = newsRepository.findAllByCommentsTextContainingIgnoreCase(commentText, pageable);
        return newsMapper.mapToNewsResponses(news);
    }

    @Override
    public List<NewsResponse> findAllByCommentsUsername(String commentUsername, Pageable pageable) {
        List<News> news = newsRepository.findAllByCommentsUsernameContainingIgnoreCase(commentUsername, pageable);
        return newsMapper.mapToNewsResponses(news);
    }

    @Override
    @Cacheable(key = "#id", value = "news")
    public CommentNewsResponse findById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("News with id = [%d] not found".formatted(id)));

        return newsMapper.mapToCommentNewsResponse(news);
    }

    @Override
    @Transactional
    @CachePut(key = "#result.id", value = "news")
    public CommentNewsResponse save(NewsRequest newsRequest, Principal principal) {
        News news = newsMapper.mapToNews(newsRequest, principal.getName());

        News savedNews = newsRepository.save(news);

        return newsMapper.mapToCommentNewsResponse(savedNews);
    }

    @Override
    @Transactional
    @CachePut(key = "#id", value = "news")
    public CommentNewsResponse update(Long id, NewsRequest newsRequest) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("News with id = [%d] not found".formatted(id)));

        if (isAccessRole("JOURNALIST") && !isAccessUsername(news.getUsername())) {
            throw new AccessDeniedException("Journalist can only update his news");
        }

        newsMapper.mapUpdateFieldsToNews(newsRequest, news);
        News updatedNews = newsRepository.save(news);

        return newsMapper.mapToCommentNewsResponse(updatedNews);
    }

    @Override
    @Transactional
    @CachePut(key = "#id", value = "news")
    public CommentNewsResponse updateText(Long id, NewsTextRequest newsTextRequest) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("News with id = [%d] not found".formatted(id)));

        if (isAccessRole("JOURNALIST") && !isAccessUsername(news.getUsername())) {
            throw new AccessDeniedException("Journalist can only update his news text");
        }

        newsMapper.mapUpdateTextFieldToNews(newsTextRequest, news);
        News updatedNews = newsRepository.save(news);

        return newsMapper.mapToCommentNewsResponse(updatedNews);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id", value = "news")
    public void deleteById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("News with id = [%d] not found".formatted(id)));

        if (isAccessRole("JOURNALIST") && !isAccessUsername(news.getUsername())) {
            throw new AccessDeniedException("Journalist can only delete his news");
        }

        newsRepository.deleteById(id);
    }
}
