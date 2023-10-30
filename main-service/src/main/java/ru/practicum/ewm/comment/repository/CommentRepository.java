package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndDeleted(Long commentId, Boolean deleted);

    List<Comment> findAllByEventIdAndDeletedOrderById(Long eventId, Boolean deleted, Pageable pageable);

    List<Comment> findAllByAuthorIdAndDeletedOrderById(Long authorId, Boolean deleted, Pageable pageable);

    @Modifying
    @Query("UPDATE Comment c " +
            "SET c.deleted = TRUE " +
            "WHERE c.deleted = FALSE " +
            "AND c.id = :commentId")
    Integer MarkCommentsAsDeletedBytId(Long commentId);

}
