package ru.clevertec.newsservice.util.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.response.comment.NewsCommentResponse;
import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.util.TestBuilder;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsCommentResponse")
public class NewsCommentResponseTestBuilder implements TestBuilder<NewsCommentResponse> {

    private Long id = 1L;
    private String text = "";
    private LocalDateTime time = LocalDateTime.now();
    private String username = "";
    private NewsResponse news = new NewsResponse();

    @Override
    public NewsCommentResponse build() {
        final NewsCommentResponse newsCommentResponse = new NewsCommentResponse();

        newsCommentResponse.setId(id);
        newsCommentResponse.setText(text);
        newsCommentResponse.setTime(time);
        newsCommentResponse.setUsername(username);
        newsCommentResponse.setNews(news);

        return newsCommentResponse;
    }
}
