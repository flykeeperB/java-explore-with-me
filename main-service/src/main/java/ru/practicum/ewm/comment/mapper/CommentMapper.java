package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentVersionDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentVersion;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface CommentMapper {
    Comment mapToComment(NewCommentDto newCommentDto,
                         User author,
                         Event event,
                         Comment replyToComment);

    CommentDto mapToCommentDto(Comment comment);

    List<CommentDto> mapToCommentDto(List<Comment> comments);

    CommentVersion mapToCommentVersion(Comment comment);

    CommentVersionDto mapToCommentVersionDto(CommentVersion commentVersion);

    List<CommentVersionDto> mapToCommentVersionDto(List<CommentVersion> commentsHistory);
}
