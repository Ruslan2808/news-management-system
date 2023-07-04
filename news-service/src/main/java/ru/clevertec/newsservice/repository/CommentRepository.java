package ru.clevertec.newsservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.clevertec.newsservice.entity.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Interface to perform operations with object of type {@link Comment}
 *
 * @author Ruslan Kantsevich
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Finds all objects of type {@link Comment} by news id with the possibility of pagination
     *
     * @param id       id of news the comments belong to
     * @param pageable object of type {@link Pageable} containing pagination and sorting parameters
     *                 (page, size and sort)
     * @return list objects of type {@link Comment}
     */
    List<Comment> findAllByNewsId(Long id, Pageable pageable);

    /**
     * Finds news comment object of type {@link Comment}
     *
     * @param commentId comment id
     * @param newsId    news id
     * @return object of type {@link Optional<Comment>}
     */
    Optional<Comment> findByIdAndNewsId(Long commentId, Long newsId);

}
