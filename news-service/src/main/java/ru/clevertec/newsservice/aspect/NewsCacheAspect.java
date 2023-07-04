package ru.clevertec.newsservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import ru.clevertec.newsservice.cache.Cache;
import ru.clevertec.newsservice.cache.factory.CacheFactory;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;

import java.util.Optional;

/**
 * Aspect class for news caching
 *
 * @author Ruslan Kantsevich
 * */
@Aspect
@Component
@ConditionalOnBean(CacheFactory.class)
public class NewsCacheAspect {

    private final Cache<Long, CommentNewsResponse> newsCache;

    public NewsCacheAspect(CacheFactory cacheFactory) {
        this.newsCache = cacheFactory.createCache();
    }

    /**
     * Caches the news when getting it by id. If the news is not in the cache then it is taken
     * from the database and added to the cache. Otherwise, it is taken from the cache
     *
     * @param joinPoint object of type {@link ProceedingJoinPoint} exposes findById method in order to support around advice
     * @return object of type {@link CommentNewsResponse} which is a cached news
     * */
    @Around("execution(* ru.clevertec.newsservice.service.NewsService.findById(..))")
    public CommentNewsResponse aroundFindById(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];
        Optional<CommentNewsResponse> cachedNews = newsCache.get(id);

        if (cachedNews.isEmpty()) {
            CommentNewsResponse news = (CommentNewsResponse) joinPoint.proceed();
            newsCache.put(id, news);

            return news;
        }

        return cachedNews.get();
    }

    /**
     * Caches the news when saving it. First a news insert to database and then in cache
     *
     * @param joinPoint object of type {@link ProceedingJoinPoint} exposes save method in order to support around advice
     * @return object of type {@link CommentNewsResponse} which is a cached news
     * */
    @Around("execution(* ru.clevertec.newsservice.service.NewsService.save(..))")
    public CommentNewsResponse aroundSave(ProceedingJoinPoint joinPoint) throws Throwable {
        CommentNewsResponse news = (CommentNewsResponse) joinPoint.proceed();
        newsCache.put(news.getId(), news);

        return news;
    }

    /**
     * Caches the news when updating it. First a news updating in database and then in cache
     *
     * @param joinPoint object of type {@link ProceedingJoinPoint} exposes update method in order to support around advice
     * @return object of type {@link CommentNewsResponse} which is a cached news
     * */
    @Around("execution(* ru.clevertec.newsservice.service.NewsService.update*(..))")
    public CommentNewsResponse aroundUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];

        CommentNewsResponse news = (CommentNewsResponse) joinPoint.proceed();
        newsCache.put(id, news);

        return news;
    }

    /**
     * Caches the news when deleting it by id. First a news deleting in database and then in cache if it's there
     *
     * @param joinPoint object of type {@link ProceedingJoinPoint} exposes deleteById method in order to support around advice
     * @return object result of execution of the delete method
     * */
    @Around("execution(* ru.clevertec.newsservice.service.NewsService.deleteById(..))")
    public Object aroundDeleteById(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];

        Object object = joinPoint.proceed();

        if (newsCache.containsKey(id)) {
            newsCache.remove(id);
        }

        return object;
    }
}
