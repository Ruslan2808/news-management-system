package ru.clevertec.newsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import org.springframework.stereotype.Component;

import ru.clevertec.newsservice.dto.filter.NewsFilter;
import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.dto.request.news.NewsTextRequest;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.entity.News;

import java.util.List;

@Component
@Mapper(uses = CommentMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsMapper {

    News mapToNews(NewsFilter newsFilter);
    News mapToNews(NewsRequest newsRequest, String username);
    CommentNewsResponse mapToCommentNewsResponse(News news);
    List<NewsResponse> mapToNewsResponses(List<News> news);
    void mapUpdateFieldsToNews(NewsRequest newsRequest, @MappingTarget News news);
    void mapUpdateTextFieldToNews(NewsTextRequest newsTextRequest, @MappingTarget News news);

}
