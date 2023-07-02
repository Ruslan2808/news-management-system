package ru.clevertec.newsservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.clevertec.newsservice.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByNewsId(Long id, Pageable pageable);
    Optional<Comment> findByIdAndNewsId(Long commentId, Long newsId);

}
