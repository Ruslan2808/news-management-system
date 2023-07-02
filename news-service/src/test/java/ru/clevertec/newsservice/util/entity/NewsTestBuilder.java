package ru.clevertec.newsservice.util.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.util.TestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "news")
public class NewsTestBuilder implements TestBuilder<News> {

    private Long id = 1L;
    private String title = "";
    private String text = "";
    private LocalDateTime time = LocalDateTime.now();
    private String username = "";
    private List<Comment> comments = new ArrayList<>();

    @Override
    public News build() {
        final News news = new News();

        news.setId(id);
        news.setTitle(title);
        news.setText(text);
        news.setTime(time);
        news.setUsername(username);
        news.setComments(comments);

        return news;
    }
}
