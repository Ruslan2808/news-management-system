package ru.clevertec.newsservice.util.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.request.comment.CommentRequest;
import ru.clevertec.newsservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "commentRequest")
public class CommentRequestTestBuilder implements TestBuilder<CommentRequest> {

    private String text = "";
    private Long newsId = 1L;

    @Override
    public CommentRequest build() {
        final CommentRequest commentRequest = new CommentRequest();

        commentRequest.setText(text);
        commentRequest.setNewsId(newsId);

        return commentRequest;
    }
}
