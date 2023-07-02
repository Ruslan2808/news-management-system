package ru.clevertec.newsservice.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import ru.clevertec.newsservice.entity.Comment;

import java.time.LocalDateTime;

public class CommentListener {

    @PrePersist
    public void beforeSave(Comment comment) {
        comment.setTime(LocalDateTime.now());
    }

    @PreUpdate
    public void beforeUpdate(Comment comment) {
        comment.setTime(LocalDateTime.now());
    }
}
