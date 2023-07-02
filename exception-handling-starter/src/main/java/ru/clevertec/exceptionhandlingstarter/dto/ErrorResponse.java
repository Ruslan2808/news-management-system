package ru.clevertec.exceptionhandlingstarter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @DateTimeFormat(iso = DATE_TIME)
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private Integer status;
    private HttpStatus error;
    private String path;

}
