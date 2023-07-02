package ru.clevertec.newsservice.util.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.request.news.NewsRequest;
import ru.clevertec.newsservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsRequest")
public class NewsRequestTestBuilder implements TestBuilder<NewsRequest> {

    private String title = "";
    private String text = "";

    @Override
    public NewsRequest build() {
        final NewsRequest newsRequest = new NewsRequest();

        newsRequest.setTitle(title);
        newsRequest.setText(text);

        return newsRequest;
    }
}
