package ru.clevertec.newsservice.util.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.entity.Comment;
import ru.clevertec.newsservice.entity.News;
import ru.clevertec.newsservice.util.TestBuilder;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "comment")
public class CommentTestBuilder implements TestBuilder<Comment> {

    private Long id = 1L;
    private String text = "";
    private LocalDateTime time = LocalDateTime.now();
    private String username = "";
    private News news = new News();

    @Override
    public Comment build() {
        final Comment comment = new Comment();

        comment.setId(id);
        comment.setText(text);
        comment.setTime(time);
        comment.setUsername(username);
        comment.setNews(news);

        return comment;
    }
}
