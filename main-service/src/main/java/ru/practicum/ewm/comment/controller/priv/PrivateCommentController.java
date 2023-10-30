package ru.practicum.ewm.comment.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentVersionDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @NotNull Long userId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.createCommentByUser(userId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByUser(@PathVariable @NotNull Long userId,
                                          @RequestBody @Valid UpdateCommentDto updateCommentDto) {

        return commentService.updateCommentByUser(userId, updateCommentDto);
    }

    @GetMapping
    public List<CommentDto> getAllCommentsOfUser(@PathVariable @NotNull Long userId,
                                                 @RequestParam(required = false, defaultValue = "0")
                                                 @PositiveOrZero Integer from,
                                                 @RequestParam(required = false, defaultValue = "10")
                                                 @Positive Integer size) {

        return commentService.getCommentsByUserId(userId, size, from);
    }

    @GetMapping("/history/{commentId}")
    public List<CommentVersionDto> getHistoryOfComment(@PathVariable @NotNull Long userId,
                                                       @PathVariable @NotNull Long commentId,
                                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {

        return commentService.getCommentsHistory(userId, commentId, size, from);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @NotNull Long userId,
                              @PathVariable @NotNull Long commentId) {

        commentService.deleteCommentByUser(userId, commentId);
    }
}
