package ru.clevertec.newsservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.clevertec.newsservice.entity.News;

import java.util.List;

/**
 * Interface to perform operations with object of type {@link News}
 *
 * @author Ruslan Kantsevich
 */
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Finds all objects of type {@link News} by comment text with the possibility of pagination
     *
     * @param commentText news comment text
     * @param pageable    object of type {@link Pageable} containing pagination and sorting parameters
     *                    (page, size and sort)
     * @return list objects of type {@link News}
     */
    List<News> findAllByCommentsTextContainingIgnoreCase(String commentText, Pageable pageable);

    /**
     * Finds all objects of type {@link News} by comment username with the possibility of pagination
     *
     * @param commentUsername news comment username
     * @param pageable        object of type {@link Pageable} containing pagination and sorting parameters
     *                        (page, size and sort)
     * @return list objects of type {@link News}
     */
    List<News> findAllByCommentsUsernameContainingIgnoreCase(String commentUsername, Pageable pageable);
}
