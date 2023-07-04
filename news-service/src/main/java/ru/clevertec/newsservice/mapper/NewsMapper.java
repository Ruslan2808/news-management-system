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

/**
 * Class that provides methods for mapping object storing news data
 *
 * @author Ruslan Kantsevich
 */
@Component
@Mapper(uses = CommentMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsMapper {

    /**
     * Mappings object of type {@link NewsFilter} storing news data for filtering to
     * object of type {@link News}
     *
     * @param newsFilter object of type {@link NewsFilter} containing information about
     *                   title, text and username of news for filtering
     * @return object of type {@link News} containing news information
     */
    News mapToNews(NewsFilter newsFilter);

    /**
     * Mappings object of type {@link NewsRequest} storing news data for saving to
     * object of type {@link News}
     *
     * @param newsRequest object of type {@link NewsFilter} containing information about
     *                   title and text of news for saving
     * @param username username who owns the news
     * @return object of type {@link News} containing news information
     */
    News mapToNews(NewsRequest newsRequest, String username);

    /**
     * Mappings object of type {@link News} storing news data to
     * object of type {@link CommentNewsResponse} containing comments
     *
     * @param news object of type {@link News} containing news information
     * @return object of type {@link CommentNewsResponse} containing news information with comments
     */
    CommentNewsResponse mapToCommentNewsResponse(News news);

    /**
     * Mappings list objects of type {@link News} storing news data to list
     * object of type {@link NewsResponse} that does not contain comments
     *
     * @param news list objects of type {@link News} containing news information
     * @return object of type {@link NewsResponse} containing news information without comments
     */
    List<NewsResponse> mapToNewsResponses(List<News> news);

    /**
     * Updates the fields of object of type {@link News} to fields from object of type {@link NewsRequest}
     *
     * @param newsRequest object of type {@link NewsRequest} containing fields to update
     * @param news target object of type {@link News} whose fields need to be updated
     * */
    void mapUpdateFieldsToNews(NewsRequest newsRequest, @MappingTarget News news);

    /**
     * Updates the text field of object of type {@link News} to text field from object of type {@link NewsTextRequest}
     *
     * @param newsTextRequest object of type {@link NewsTextRequest} containing text field to update
     * @param news target object of type {@link News} whose text field need to be updated
     * */
    void mapUpdateTextFieldToNews(NewsTextRequest newsTextRequest, @MappingTarget News news);
}
