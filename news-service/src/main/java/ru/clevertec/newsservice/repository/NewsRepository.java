package ru.clevertec.newsservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.clevertec.newsservice.entity.News;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findAllByCommentsTextContainingIgnoreCase(String commentText, Pageable pageable);
    List<News> findAllByCommentsUsernameContainingIgnoreCase(String commentUsername, Pageable pageable);

}
