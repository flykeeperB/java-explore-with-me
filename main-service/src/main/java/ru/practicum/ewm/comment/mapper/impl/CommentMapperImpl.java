package ru.practicum.ewm.comment.mapper.impl;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentVersionDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentVersion;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentMapperImpl implements CommentMapper {
    @Override
    public Comment mapToComment(NewCommentDto newCommentDto,
                                User author,
                                Event event,
                                Comment replyToComment) {
        return Comment.builder()
                .author(author)
                .event(event)
                .replyToComment(replyToComment)
                .actualText(newCommentDto.getText())
                .build();
    }

    @Override
    public CommentDto mapToCommentDto(Comment comment) {
        CommentDto result = CommentDto.builder()
                .id(comment.getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .eventId(comment.getEvent().getId())
                .lastChanged(comment.getLastChanged())
                .text(comment.getActualText())
                .build();
        if (comment.getReplyToComment() != null) {
            result.setReplyToCommentId(comment.getReplyToComment().getId());
        }

        return result;
    }

    @Override
    public List<CommentDto> mapToCommentDto(List<Comment> comments) {
        return comments.stream().map(this::mapToCommentDto).collect(Collectors.toList());
    }

    @Override
    public CommentVersion mapToCommentVersion(Comment comment) {
        return CommentVersion.builder()
                .comment(comment)
                .changed(comment.getLastChanged())
                .replyToComment(comment.getReplyToComment())
                .text(comment.getActualText())
                .build();
    }

    @Override
    public CommentVersionDto mapToCommentVersionDto(CommentVersion commentVersion) {
        CommentVersionDto result = CommentVersionDto.builder()
                .id(commentVersion.getId())
                .changed(commentVersion.getChanged())
                .commentId(commentVersion.getComment().getId())
                .text(commentVersion.getText())
                .build();

        if (commentVersion.getReplyToComment() != null) {
            result.setReplyToCommentId(commentVersion.getReplyToComment().getId());
        }

        return result;
    }

    @Override
    public List<CommentVersionDto> mapToCommentVersionDto(List<CommentVersion> commentsHistory) {
        return commentsHistory.stream().map(this::mapToCommentVersionDto).collect(Collectors.toList());
    }
}
