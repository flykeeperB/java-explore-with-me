package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentVersionDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createCommentByUser(Long userId, NewCommentDto newCommentDto);

    CommentDto updateCommentByUser(Long userId, UpdateCommentDto updateCommentDto);

    void deleteCommentByUser(Long userId, Long commentId);

    CommentDto getCommentById(Long commentId);

    List<CommentVersionDto> getCommentsHistory(Long userId, Long commentId, int size, int from);

    List<CommentVersionDto> getCommentsHistoryForAdmin(Long commentId, int size, int from);

    List<CommentDto> getCommentsByUserId(Long userId, int size, int from);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, int size, int from);
}
