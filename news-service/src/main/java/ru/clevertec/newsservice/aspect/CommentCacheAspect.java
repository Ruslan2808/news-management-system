package ru.clevertec.newsservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import ru.clevertec.newsservice.cache.Cache;
import ru.clevertec.newsservice.cache.factory.CacheFactory;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;

import java.util.Optional;

@Aspect
@Component
@ConditionalOnBean(CacheFactory.class)
public class CommentCacheAspect {

    private final Cache<Long, NewsCommentResponse> commentCache;

    public CommentCacheAspect(CacheFactory cacheFactory) {
        this.commentCache = cacheFactory.createCache();
    }

    @Around("execution(* ru.clevertec.newsservice.service.CommentService.findById*(..))")
    public NewsCommentResponse aroundFindById(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];
        Optional<NewsCommentResponse> cachedComment = commentCache.get(id);

        if (cachedComment.isEmpty()) {
            NewsCommentResponse comment = (NewsCommentResponse) joinPoint.proceed();
            commentCache.put(id, comment);

            return comment;
        }

        return cachedComment.get();
    }

    @Around("execution(* ru.clevertec.newsservice.service.CommentService.save(..))")
    public NewsCommentResponse aroundSave(ProceedingJoinPoint joinPoint) throws Throwable {
        NewsCommentResponse comment = (NewsCommentResponse) joinPoint.proceed();
        commentCache.put(comment.getId(), comment);

        return comment;
    }

    @Around("execution(* ru.clevertec.newsservice.service.CommentService.update*(..))")
    public NewsCommentResponse aroundUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];

        NewsCommentResponse comment = (NewsCommentResponse) joinPoint.proceed();
        commentCache.put(id, comment);

        return comment;
    }

    @Around("execution(* ru.clevertec.newsservice.service.CommentService.deleteById(..))")
    public Object aroundDeleteById(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];

        Object object = joinPoint.proceed();

        if (commentCache.containsKey(id)) {
            commentCache.remove(id);
        }

        return object;
    }
}
