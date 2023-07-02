package ru.clevertec.newsservice.util.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.response.news.NewsResponse;
import ru.clevertec.newsservice.util.TestBuilder;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsResponse")
public class NewsResponseTestBuilder implements TestBuilder<NewsResponse> {

    private Long id = 1L;
    private String title = "";
    private String text = "";
    private LocalDateTime time = LocalDateTime.now();
    private String username = "";

    @Override
    public NewsResponse build() {
        final NewsResponse newsResponse = new NewsResponse();

        newsResponse.setId(id);
        newsResponse.setTitle(title);
        newsResponse.setText(text);
        newsResponse.setTime(time);
        newsResponse.setUsername(username);

        return newsResponse;
    }
}
