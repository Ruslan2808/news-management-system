package ru.clevertec.newsservice.util.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.response.comment.CommentResponse;
import ru.clevertec.newsservice.util.TestBuilder;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "commentResponse")
public class CommentResponseTestBuilder implements TestBuilder<CommentResponse> {

    private Long id = 1L;
    private String text = "";
    private LocalDateTime time = LocalDateTime.now();
    private String username = "";

    @Override
    public CommentResponse build() {
        final CommentResponse commentResponse = new CommentResponse();

        commentResponse.setId(id);
        commentResponse.setText(text);
        commentResponse.setTime(time);
        commentResponse.setUsername(username);

        return commentResponse;
    }
}
