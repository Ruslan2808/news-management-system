package ru.clevertec.newsservice.util.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.request.news.NewsTextRequest;
import ru.clevertec.newsservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsTextRequest")
public class NewsTextRequestTestBuilder implements TestBuilder<NewsTextRequest> {

    private String text = "";

    @Override
    public NewsTextRequest build() {
        final NewsTextRequest newsTextRequest = new NewsTextRequest();

        newsTextRequest.setText(text);

        return newsTextRequest;
    }
}
