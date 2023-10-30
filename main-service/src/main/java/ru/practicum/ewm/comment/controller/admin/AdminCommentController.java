package ru.practicum.ewm.comment.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentVersionDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable @NotNull Long commentId) {

        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/history/{commentId}")
    public List<CommentVersionDto> getHistoryOfCommentByAdmin(@PathVariable @NotNull Long commentId,
                                                              @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                              @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {

        return commentService.getCommentsHistoryForAdmin(commentId, size, from);
    }
}
