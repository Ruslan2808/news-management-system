package ru.clevertec.newsservice.util.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.dto.response.news.CommentNewsResponse;
import ru.clevertec.newsservice.util.TestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "commentNewsResponse")
public class CommentNewsResponseTestBuilder implements TestBuilder<CommentNewsResponse> {

    private Long id = 1L;
    private String title = "";
    private String text = "";
    private LocalDateTime time = LocalDateTime.now();
    private String username = "";
    private List<CommentResponse> comments = new ArrayList<>();

    @Override
    public CommentNewsResponse build() {
        final CommentNewsResponse commentNewsResponse = new CommentNewsResponse();

        commentNewsResponse.setId(id);
        commentNewsResponse.setTitle(title);
        commentNewsResponse.setText(text);
        commentNewsResponse.setTime(time);
        commentNewsResponse.setUsername(username);
        commentNewsResponse.setComments(comments);

        return commentNewsResponse;
    }
}
