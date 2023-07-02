package ru.clevertec.newsservice.dto.response.news;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse implements Serializable {

    private Long id;
    private String title;
    private String text;

    @DateTimeFormat(iso = DATE_TIME)
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;
    private String username;

}
