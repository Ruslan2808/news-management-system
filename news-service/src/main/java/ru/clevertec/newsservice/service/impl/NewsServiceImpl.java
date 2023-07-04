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

/**
 * An implementation of the {@link NewsService} interface for performing operations with {@link News}
 *
 * @author Ruslan Kantsevich
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "newsCache")
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;
    private final NewsRepository newsRepository;

    /**
     * Finds all objects of type {@link News} with the possibility of filtering and pagination
     *
     * @param newsFilter object of type {@link NewsFilter} containing information about
     *                   title, text and username of news for filtering
     * @param pageable   object of type {@link Pageable} containing pagination and sorting parameters
     *                   (page, size and sort)
     * @return list objects of type {@link NewsResponse}
     */
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

    /**
     * Finds all objects of type {@link News} by comment text with the possibility of filtering and pagination
     *
     * @param commentText news comment text
     * @param pageable    object of type {@link Pageable} containing pagination and sorting parameters
     *                    (page, size and sort)
     * @return list objects of type {@link NewsResponse}
     */
    @Override
    public List<NewsResponse> findAllByCommentsText(String commentText, Pageable pageable) {
        List<News> news = newsRepository.findAllByCommentsTextContainingIgnoreCase(commentText, pageable);
        return newsMapper.mapToNewsResponses(news);
    }

    /**
     * Finds all objects of type {@link News} by comment username with the possibility of pagination
     *
     * @param commentUsername news comment username
     * @param pageable        object of type {@link Pageable} containing pagination and sorting parameters
     *                        (page, size and sort)
     * @return list objects of type {@link NewsResponse}
     */
    @Override
    public List<NewsResponse> findAllByCommentsUsername(String commentUsername, Pageable pageable) {
        List<News> news = newsRepository.findAllByCommentsUsernameContainingIgnoreCase(commentUsername, pageable);
        return newsMapper.mapToNewsResponses(news);
    }

    /**
     * Finds news by id or throws a {@link NewsNotFoundException}
     * if the news with the given id is not found in the database
     *
     * @param id the news id
     * @return object of type {@link CommentNewsResponse} with given id
     * @throws NewsNotFoundException if the news with the given id is not found in the database
     */
    @Override
    @Cacheable(key = "#id", value = "news")
    public CommentNewsResponse findById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("News with id = [%d] not found".formatted(id)));

        return newsMapper.mapToCommentNewsResponse(news);
    }

    /**
     * Saves the news in the database
     *
     * @param newsRequest object of type {@link NewsRequest} to save
     * @param principal   object of type {@link Principal} containing information about authenticated username
     * @return the saved news of type {@link CommentNewsResponse}
     */
    @Override
    @Transactional
    @CachePut(key = "#result.id", value = "news")
    public CommentNewsResponse save(NewsRequest newsRequest, Principal principal) {
        News news = newsMapper.mapToNews(newsRequest, principal.getName());

        News savedNews = newsRepository.save(news);

        return newsMapper.mapToCommentNewsResponse(savedNews);
    }

    /**
     * Updates the news with the given id in the database or throws a {@link NewsNotFoundException}
     * if the news with the given id is not found or throws a {@link AccessDeniedException}
     * if the user tries to update not his news
     *
     * @param id          the id of the updated news
     * @param newsRequest the news of type {@link NewsRequest} with data to update an existing news
     * @return the updated news of type {@link CommentNewsResponse}
     * @throws NewsNotFoundException if the news with the given id is not found in the database
     * @throws AccessDeniedException iif the user tries to update not his news
     */
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

    /**
     * Updates the news text with the given id in the database or throws a {@link NewsNotFoundException}
     * if the news with the given id is not found or throws a {@link AccessDeniedException}
     * if the user tries to update not his news
     *
     * @param id              the id of the updated news
     * @param newsTextRequest the news of type {@link NewsTextRequest} with text field to update an existing news
     * @return the updated news of type {@link CommentNewsResponse}
     * @throws NewsNotFoundException if the news with the given id is not found in the database
     * @throws AccessDeniedException iif the user tries to update not his news
     */
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

    /**
     * Deletes the news with the given id from the database or throws a {@link NewsNotFoundException}
     * if the news with the given id is not found or throws a {@link AccessDeniedException}
     * if the user tries to delete not his news
     *
     * @param id the id of the news to be deleted
     * @throws NewsNotFoundException if the news with the given id is not found in the database
     * @throws AccessDeniedException iif the user tries to delete not his news
     */
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
