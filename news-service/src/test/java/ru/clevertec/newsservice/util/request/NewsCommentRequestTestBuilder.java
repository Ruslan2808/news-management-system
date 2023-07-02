package ru.clevertec.newsservice.util.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.request.comment.NewsCommentRequest;
import ru.clevertec.newsservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsCommentRequest")
public class NewsCommentRequestTestBuilder implements TestBuilder<NewsCommentRequest> {

    private String text = "";

    @Override
    public NewsCommentRequest build() {
        final NewsCommentRequest newsCommentRequest = new NewsCommentRequest();

        newsCommentRequest.setText(text);

        return newsCommentRequest;
    }
}
