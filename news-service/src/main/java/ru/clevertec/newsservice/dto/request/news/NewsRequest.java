package ru.clevertec.newsservice.dto.request.news;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {

    @NotBlank(message = "News title cannot be empty")
    private String title;

    @NotBlank(message = "News text cannot be empty")
    private String text;

}
