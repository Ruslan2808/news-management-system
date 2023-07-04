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

import ru.clevertec.exceptionhandlingstarter.exception.CommentNotFoundException;
import ru.clevertec.exceptionhandlingstarter.exception.NewsNotFoundException;
import ru.clevertec.newsservice.dto.filter.CommentFilter;
import ru.clevertec.newsservice.dto.filter.NewsFilter;
import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.mapper.CommentMapper;
import ru.clevertec.newsservice.repository.CommentRepository;
import ru.clevertec.newsservice.repository.NewsRepository;
import ru.clevertec.newsservice.service.CommentService;
import ru.clevertec.newsservice.service.NewsService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

import static ru.clevertec.newsservice.security.util.SecurityUtil.isAccessRole;
import static ru.clevertec.newsservice.security.util.SecurityUtil.isAccessUsername;

/**
 * An implementation of the {@link CommentService} interface for performing operations with {@link Comment}
 *
 * @author Ruslan Kantsevich
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "commentsCache")
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;

    /**
     * Finds all objects of type {@link Comment} with the possibility of filtering and pagination
     *
     * @param commentFilter object of type {@link CommentFilter} containing information about
     *                      text and username of news for filtering
     * @param pageable      object of type {@link Pageable} containing pagination and sorting parameters
     *                      (page, size and sort)
     * @return list objects of type {@link CommentResponse}
     */
    @Override
    public List<CommentResponse> findAll(CommentFilter commentFilter, Pageable pageable) {
        ExampleMatcher commentMatcher = ExampleMatcher.matching()
                .withMatcher("text", contains().ignoreCase())
                .withMatcher("username", contains().ignoreCase());

        Comment filteredComment = commentMapper.mapToComment(commentFilter);
        Example<Comment> commentExample = Example.of(filteredComment, commentMatcher);

        List<Comment> comments = commentRepository.findAll(commentExample, pageable).getContent();

        return commentMapper.mapToCommentResponses(comments);
    }

    /**
     * Finds all objects of type {@link Comment} by news id with the possibility of pagination
     *
     * @param id       id of news the comments belong to
     * @param pageable object of type {@link Pageable} containing pagination and sorting parameters
     *                 (page, size and sort)
     * @return list objects of type {@link CommentResponse}
     */
    @Override
    public List<CommentResponse> findAllByNewsId(Long id, Pageable pageable) {
        List<Comment> comments = commentRepository.findAllByNewsId(id, pageable);
        return commentMapper.mapToCommentResponses(comments);
    }

    /**
     * Finds comment by id or throws a {@link CommentNotFoundException}
     * if the comment with the given id is not found in the database
     *
     * @param id the comment id
     * @return object of type {@link NewsCommentResponse} with given id
     * @throws CommentNotFoundException if the comment with the given id is not found in the database
     */
    @Override
    @Cacheable(key = "#id", value = "comment")
    public NewsCommentResponse findById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id = [%d] not found".formatted(id)));

        return commentMapper.mapToNewsCommentResponse(comment);
    }

    /**
     * Finds news comment object of type {@link Comment}
     *
     * @param commentId comment id
     * @param newsId    news id
     * @return object of type {@link NewsCommentResponse}
     */
    @Override
    @Cacheable(key = "#commentId", value = "comment")
    public NewsCommentResponse findByIdAndNewsId(Long commentId, Long newsId) {
        Comment comment = commentRepository.findByIdAndNewsId(commentId, newsId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id = [%d] news with id = [%d] not found".formatted(commentId, newsId)));

        return commentMapper.mapToNewsCommentResponse(comment);
    }

    /**
     * Saves the comment in the database
     *
     * @param commentRequest object of type {@link CommentRequest} to save
     * @param principal   object of type {@link Principal} containing information about authenticated username
     * @return the saved news of type {@link NewsCommentResponse}
     * @throws NewsNotFoundException if the news with the given id is not found in the database
     */
    @Override
    @Transactional
    @CachePut(key = "#result.id", value = "comment")
    public NewsCommentResponse save(CommentRequest commentRequest, Principal principal) {
        News news = newsRepository.findById(commentRequest.getNewsId())
                .orElseThrow(() -> new NewsNotFoundException("News with id = [%d] not found".formatted(commentRequest.getNewsId())));

        Comment comment = commentMapper.mapToComment(commentRequest, principal.getName());
        comment.setNews(news);

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.mapToNewsCommentResponse(savedComment);
    }

    /**
     * Updates the comment with the given id in the database or throws a {@link CommentNotFoundException}
     * if the comment with the given id is not found or throws a {@link AccessDeniedException}
     * if the user tries to update not his comment
     *
     * @param id                 the id of the updated comment
     * @param newsCommentRequest the comment of type {@link NewsCommentRequest} with data to update an existing comment
     * @return the updated comment of type {@link NewsCommentResponse}
     * @throws CommentNotFoundException if the comment with the given id is not found in the database
     * @throws AccessDeniedException iif the user tries to update not his comment
     */
    @Override
    @Transactional
    @CachePut(key = "#id", value = "comment")
    public NewsCommentResponse update(Long id, NewsCommentRequest newsCommentRequest) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id = [%d] not found".formatted(id)));

        if (isAccessRole("SUBSCRIBER") && !isAccessUsername(comment.getUsername())) {
            throw new AccessDeniedException("Subscriber can only update his comments");
        }

        commentMapper.mapUpdateFieldsToComment(newsCommentRequest, comment);
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.mapToNewsCommentResponse(updatedComment);
    }

    /**
     * Deletes the comment with the given id from the database or throws a {@link CommentNotFoundException}
     * if the comment with the given id is not found or throws a {@link AccessDeniedException}
     * if the user tries to delete not his comment
     *
     * @param id the id of the comment to be deleted
     * @throws NewsNotFoundException if the comment with the given id is not found in the database
     * @throws AccessDeniedException iif the user tries to delete not his comment
     */
    @Override
    @Transactional
    @CacheEvict(key = "#id", value = "comment")
    public void deleteById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id = [%d] not found".formatted(id)));

        if (isAccessRole("SUBSCRIBER") && !isAccessUsername(comment.getUsername())) {
            throw new AccessDeniedException("Subscriber can only delete his comments");
        }

        commentRepository.deleteById(id);
    }
}
