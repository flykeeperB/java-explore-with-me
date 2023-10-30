package ru.practicum.ewm.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDto {
    @NotNull
    private Long id;

    private Long replyToCommentId;

    @NotBlank
    @Size(min = 5, max = 1000)
    private String text;
}
