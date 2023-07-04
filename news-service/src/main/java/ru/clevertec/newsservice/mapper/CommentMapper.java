package ru.clevertec.newsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import org.springframework.stereotype.Component;

import ru.clevertec.newsservice.dto.filter.CommentFilter;
import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;
import ru.clevertec.newsservice.entity.Comment;

import java.util.List;

/**
 * Class that provides methods for mapping object storing comments data
 *
 * @author Ruslan Kantsevich
 */
@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    /**
     * Mappings object of type {@link CommentFilter} storing comment data for filtering to
     * object of type {@link Comment}
     *
     * @param commentFilter object of type {@link CommentFilter} containing information about
     *                      text and username of comment for filtering
     * @return object of type {@link Comment} containing comment information
     */
    Comment mapToComment(CommentFilter commentFilter);

    /**
     * Mappings object of type {@link CommentRequest} storing comment data for saving to
     * object of type {@link Comment}
     *
     * @param commentRequest object of type {@link CommentRequest} containing information about
     *                       text and news id of comment for saving
     * @param username       username who owns the comment
     * @return object of type {@link Comment} containing comment information
     */
    Comment mapToComment(CommentRequest commentRequest, String username);

    /**
     * Mappings object of type {@link Comment} storing comment data to
     * object of type {@link NewsCommentResponse} containing news
     *
     * @param comment object of type {@link Comment} containing comment information
     * @return object of type {@link NewsCommentResponse} containing comment information with news
     */
    NewsCommentResponse mapToNewsCommentResponse(Comment comment);

    /**
     * Mappings list objects of type {@link Comment} storing comment data to list
     * object of type {@link CommentResponse} that does not contain news
     *
     * @param comments list objects of type {@link Comment} containing comment information
     * @return object of type {@link CommentResponse} containing comment information without news
     */
    List<CommentResponse> mapToCommentResponses(List<Comment> comments);

    /**
     * Updates the text field of object of type {@link Comment} to text field from object of type {@link NewsCommentRequest}
     *
     * @param newsCommentRequest object of type {@link NewsCommentRequest} containing text field to update
     * @param comment            target object of type {@link Comment} whose text field need to be updated
     */
    void mapUpdateFieldsToComment(NewsCommentRequest newsCommentRequest, @MappingTarget Comment comment);
}
