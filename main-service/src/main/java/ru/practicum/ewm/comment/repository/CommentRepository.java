package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndDeleted(Long commentId, Boolean deleted);

    List<Comment> findAllByEventIdAndDeletedOrderById(Long eventId, Boolean deleted, Pageable pageable);

    List<Comment> findAllByAuthorIdAndDeletedOrderById(Long authorId, Boolean deleted, Pageable pageable);
}