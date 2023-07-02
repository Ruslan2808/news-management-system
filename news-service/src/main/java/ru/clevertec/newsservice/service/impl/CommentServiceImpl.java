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
import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;
import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.mapper.CommentMapper;
import ru.clevertec.newsservice.repository.CommentRepository;
import ru.clevertec.newsservice.repository.NewsRepository;
import ru.clevertec.newsservice.service.CommentService;

import java.security.Principal;
import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

import static ru.clevertec.newsservice.security.util.SecurityUtil.isAccessRole;
import static ru.clevertec.newsservice.security.util.SecurityUtil.isAccessUsername;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "commentsCache")
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;

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

    @Override
    public List<CommentResponse> findAllByNewsId(Long id, Pageable pageable) {
        List<Comment> comments = commentRepository.findAllByNewsId(id, pageable);
        return commentMapper.mapToCommentResponses(comments);
    }

    @Override
    @Cacheable(key = "#id", value = "comment")
    public NewsCommentResponse findById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id = [%d] not found".formatted(id)));

        return commentMapper.mapToNewsCommentResponse(comment);
    }

    @Override
    @Cacheable(key = "#commentId", value = "comment")
    public NewsCommentResponse findByIdAndNewsId(Long commentId, Long newsId) {
        Comment comment = commentRepository.findByIdAndNewsId(commentId, newsId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id = [%d] news with id = [%d] not found".formatted(commentId, newsId)));

        return commentMapper.mapToNewsCommentResponse(comment);
    }

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
