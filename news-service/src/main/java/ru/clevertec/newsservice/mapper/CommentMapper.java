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

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment mapToComment(CommentFilter commentFilter);
    Comment mapToComment(CommentRequest commentRequest, String username);
    NewsCommentResponse mapToNewsCommentResponse(Comment comment);
    List<CommentResponse> mapToCommentResponses(List<Comment> comments);
    void mapUpdateFieldsToComment(NewsCommentRequest newsCommentRequest, @MappingTarget Comment comment);

}
