package ru.practicum.ewm.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentVersionDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentVersion;
import ru.practicum.ewm.comment.repository.CommentHistoryRepository;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    public static final String DELETE_MARK = "[* DELETED *]";

    private final CommentRepository commentRepository;
    private final CommentHistoryRepository commentHistoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final CommentMapper commentMapper;


    @Override
    @Transactional
    public CommentDto createCommentByUser(Long userId, NewCommentDto newCommentDto) {
        log.info(String.format("createCommentByUser userId-%d newCommentDto-%s", userId, newCommentDto));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        Event event = eventRepository.findById(newCommentDto.getEventId()).orElseThrow(
                () -> new NotFoundException("Cобытие не найдено."));

        Comment replyToComment = null;
        if (newCommentDto.getReplyToCommentId() != null) {
            replyToComment = commentRepository.findByIdAndDeleted(newCommentDto.getReplyToCommentId(), false).orElseThrow(
                    () -> new NotFoundException("Комментарий, на который ссылается создаваемый, не найден."));
        }

        Comment comment = commentMapper.mapToComment(newCommentDto, user, event, replyToComment);

        comment.setDeleted(false);

        Comment createdComment = saveComment(comment);
        return commentMapper.mapToCommentDto(createdComment);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByUser(Long userId, UpdateCommentDto updateCommentDto) {
        log.info(String.format("updateCommentByUser userId-%d newCommentDto-%s", userId, updateCommentDto));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        Comment comment = commentRepository.findByIdAndDeleted(updateCommentDto.getId(), false).orElseThrow(
                () -> new NotFoundException("Редактируемый комментарий не найден"));

        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new ConflictException("Обновить комментарий вправе только автор или администратор (модератор)");
        }

        comment.setActualText(updateCommentDto.getText());

        comment.setReplyToComment(null);
        if (updateCommentDto.getReplyToCommentId() != null) {
            Optional<Comment> replyComment = commentRepository
                    .findByIdAndDeleted(updateCommentDto.getReplyToCommentId(), false);

            replyComment.ifPresent(comment::setReplyToComment);
        }

        Comment updatedComment = commentRepository.save(comment);

        CommentVersion commentVersion = commentMapper.mapToCommentVersion(updatedComment);
        commentHistoryRepository.save(commentVersion);

        return commentMapper.mapToCommentDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        log.info(String.format("deleteCommentByUser userId-%d commentId-%d", userId, commentId));

        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException("Удаляемый комментарий не найден"));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new ConflictException("Удалить комментарий вправе только автор или администратор (модератор)");
        }

        deleteComment(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        log.info(String.format("getCommentById commentId-%d", commentId));

        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException("Комментарий не найден."));

        return commentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentVersionDto> getCommentsHistory(Long userId, Long commentId, int size, int from) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException("Комментарий не найден."));

        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new ConflictException("Просмотр версий комментария доступно только автору или администратору (модератору)");
        }

        List<CommentVersion> commentsHistory = commentHistoryRepository
                .findAllByCommentIdOrderByChanged(commentId, page);

        return commentMapper.mapToCommentVersionDto(commentsHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentVersionDto> getCommentsHistoryForAdmin(Long commentId, int size, int from) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);

        List<CommentVersion> commentsHistory = commentHistoryRepository
                .findAllByCommentIdOrderByChanged(commentId, page);

        return commentMapper.mapToCommentVersionDto(commentsHistory);
    }

    @Override
    @Transactional
    public List<CommentDto> getCommentsByUserId(Long userId, int size, int from) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);

        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        List<Comment> comments = commentRepository
                .findAllByAuthorIdAndDeletedOrderById(userId, false, page);

        return commentMapper.mapToCommentDto(comments);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        log.info(String.format("deleteCommentByAdmin commentId-%d", commentId));

        deleteComment(commentRepository.findByIdAndDeleted(commentId, false)
                .orElseThrow(() -> new NotFoundException("Удаляемый комментарий не найден.")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEventId(Long eventId, int size, int from) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);

        List<Comment> comments = commentRepository.findAllByEventIdAndDeletedOrderById(eventId, false, page);

        return commentMapper.mapToCommentDto(comments);
    }

    private void deleteComment(Comment deleteComment) {

        deleteComment.setActualText(DELETE_MARK);
        deleteComment.setDeleted(true);

        Comment deletedComment = commentRepository.save(deleteComment);

        CommentVersion commentVersion = commentMapper.mapToCommentVersion(deletedComment);
        commentHistoryRepository.save(commentVersion);
    }

    private Comment saveComment(Comment comment) {
        Comment result = commentRepository.save(comment);
        CommentVersion commentVersion = commentMapper.mapToCommentVersion(result);

        commentHistoryRepository.save(commentVersion);
        return result;
    }
}
