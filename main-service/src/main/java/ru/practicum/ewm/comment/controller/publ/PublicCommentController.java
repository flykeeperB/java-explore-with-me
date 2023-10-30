package ru.practicum.ewm.comment.controller.publ;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable @NotNull Long commentId) {

        return commentService.getCommentById(commentId);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getAllCommentsByEventId(@PathVariable @NotNull Long eventId,
                                                    @RequestParam(required = false, defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(required = false, defaultValue = "10")
                                                    @Positive Integer size) {

        return commentService.getCommentsByEventId(eventId, size, from);
    }
}
