package ru.clevertec.newsservice.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import ru.clevertec.newsservice.entity.Comment;

import java.time.LocalDateTime;

/**
 * Listener class providing methods to be called before inserting and updating
 * in the database an object of type {@link Comment}
 *
 * @author Ruslan Kantsevich
 * */
public class CommentListener {

    /**
     * Set the created time an object of type {@link Comment}
     *
     * @param comment object of type {@link Comment} for which you want to set the save time
     * */
    @PrePersist
    public void beforeSave(Comment comment) {
        comment.setTime(LocalDateTime.now());
    }

    /**
     * Set the updated time an object of type {@link Comment}
     *
     * @param comment object of type {@link Comment} for which you want to set the update time
     * */
    @PreUpdate
    public void beforeUpdate(Comment comment) {
        comment.setTime(LocalDateTime.now());
    }
}
