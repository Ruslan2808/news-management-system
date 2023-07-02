package ru.clevertec.newsservice.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import ru.clevertec.newsservice.entity.News;

import java.time.LocalDateTime;

public class NewsListener {

    @PrePersist
    public void beforeSave(News news) {
        news.setTime(LocalDateTime.now());
    }

    @PreUpdate
    public void beforeUpdate(News news) {
        news.setTime(LocalDateTime.now());
    }
}
