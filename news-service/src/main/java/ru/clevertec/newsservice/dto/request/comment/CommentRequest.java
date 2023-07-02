package ru.clevertec.newsservice.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Comment text cannot be empty")
    private String text;

    @NotNull(message = "News id cannot be empty")
    @Positive(message = "News id must be positive")
    private Long newsId;

}
