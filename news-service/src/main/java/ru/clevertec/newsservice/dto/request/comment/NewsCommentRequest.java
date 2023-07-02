package ru.clevertec.newsservice.dto.request.comment;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsCommentRequest {

    @NotBlank(message = "Comment text cannot be empty")
    private String text;

}
