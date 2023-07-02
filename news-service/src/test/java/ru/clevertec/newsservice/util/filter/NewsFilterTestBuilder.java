package ru.clevertec.newsservice.util.filter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.filter.NewsFilter;
import ru.clevertec.newsservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsFilter")
public class NewsFilterTestBuilder implements TestBuilder<NewsFilter> {

    private String title;
    private String text;
    private String username;

    @Override
    public NewsFilter build() {
        final NewsFilter newsFilter = new NewsFilter();

        newsFilter.setTitle(title);
        newsFilter.setText(text);
        newsFilter.setUsername(username);

        return newsFilter;
    }
}
