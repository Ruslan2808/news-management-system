package ru.clevertec.newsservice.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import ru.clevertec.newsservice.entity.News;

import java.time.LocalDateTime;

/**
 * Listener class providing methods to be called before inserting and updating
 * in the database an object of type {@link News}
 *
 * @author Ruslan Kantsevich
 * */
public class NewsListener {

    /**
     * Set the created time an object of type {@link News}
     *
     * @param news object of type {@link News} for which you want to set the save time
     * */
    @PrePersist
    public void beforeSave(News news) {
        news.setTime(LocalDateTime.now());
    }

    /**
     * Set the updated time an object of type {@link News}
     *
     * @param news object of type {@link News} for which you want to set the update time
     * */
    @PreUpdate
    public void beforeUpdate(News news) {
        news.setTime(LocalDateTime.now());
    }
}
