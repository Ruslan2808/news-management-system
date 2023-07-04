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

/**
 * Aspect class for comments caching
 *
 * @author Ruslan Kantsevich
 * */
@Aspect
@Component
@ConditionalOnBean(CacheFactory.class)
public class CommentCacheAspect {

    private final Cache<Long, NewsCommentResponse> commentCache;

    public CommentCacheAspect(CacheFactory cacheFactory) {
        this.commentCache = cacheFactory.createCache();
    }

    /**
     * Caches the comment when getting it by id. If the comment is not in the cache then it is taken
     * from the database and added to the cache. Otherwise, it is taken from the cache
     *
     * @param joinPoint object of type {@link ProceedingJoinPoint} exposes findById method in order to support around advice
     * @return object of type {@link NewsCommentResponse} which is a cached comment
     * */
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

    /**
     * Caches the comment when saving it. First a comment insert to database and then in cache
     *
     * @param joinPoint object of type {@link ProceedingJoinPoint} exposes save method in order to support around advice
     * @return object of type {@link NewsCommentResponse} which is a cached comment
     * */
    @Around("execution(* ru.clevertec.newsservice.service.CommentService.save(..))")
    public NewsCommentResponse aroundSave(ProceedingJoinPoint joinPoint) throws Throwable {
        NewsCommentResponse comment = (NewsCommentResponse) joinPoint.proceed();
        commentCache.put(comment.getId(), comment);

        return comment;
    }

    /**
     * Caches the comment when updating it. First a comment updating in database and then in cache
     *
     * @param joinPoint object of type {@link ProceedingJoinPoint} exposes update method in order to support around advice
     * @return object of type {@link NewsCommentResponse} which is a cached comment
     * */
    @Around("execution(* ru.clevertec.newsservice.service.CommentService.update*(..))")
    public NewsCommentResponse aroundUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];

        NewsCommentResponse comment = (NewsCommentResponse) joinPoint.proceed();
        commentCache.put(id, comment);

        return comment;
    }

    /**
     * Caches the comment when deleting it by id. First a comment deleting in database and then in cache if it's there
     *
     * @param joinPoint object of type {@link ProceedingJoinPoint} exposes deleteById method in order to support around advice
     * @return object result of execution of the delete method
     * */
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
