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

@Aspect
@Component
@ConditionalOnBean(CacheFactory.class)
public class NewsCacheAspect {

    private final Cache<Long, CommentNewsResponse> newsCache;

    public NewsCacheAspect(CacheFactory cacheFactory) {
        this.newsCache = cacheFactory.createCache();
    }

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

    @Around("execution(* ru.clevertec.newsservice.service.NewsService.save(..))")
    public CommentNewsResponse aroundSave(ProceedingJoinPoint joinPoint) throws Throwable {
        CommentNewsResponse news = (CommentNewsResponse) joinPoint.proceed();
        newsCache.put(news.getId(), news);

        return news;
    }

    @Around("execution(* ru.clevertec.newsservice.service.NewsService.update*(..))")
    public CommentNewsResponse aroundUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long) joinPoint.getArgs()[0];

        CommentNewsResponse news = (CommentNewsResponse) joinPoint.proceed();
        newsCache.put(id, news);

        return news;
    }

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
