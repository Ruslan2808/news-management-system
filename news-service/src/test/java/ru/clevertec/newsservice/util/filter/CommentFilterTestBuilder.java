package ru.clevertec.newsservice.util.filter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import ru.clevertec.newsservice.dto.filter.CommentFilter;
import ru.clevertec.newsservice.util.TestBuilder;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "commentFilter")
public class CommentFilterTestBuilder implements TestBuilder<CommentFilter> {

    private String text = "";
    private String username = "";

    @Override
    public CommentFilter build() {
        final CommentFilter commentFilter = new CommentFilter();

        commentFilter.setText(text);
        commentFilter.setUsername(username);

        return commentFilter;
    }
}
