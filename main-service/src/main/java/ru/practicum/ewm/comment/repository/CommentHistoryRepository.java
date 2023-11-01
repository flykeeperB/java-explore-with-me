package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comment.model.CommentVersion;

import java.util.List;

public interface CommentHistoryRepository extends JpaRepository<CommentVersion, Long> {
    List<CommentVersion> findAllByCommentIdOrderByChanged(Long commentId, Pageable pageable);
}
