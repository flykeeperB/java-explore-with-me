package ru.practicum.statsDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {

    @NotBlank(message = "Не указано наименование приложения")
    @Size(max = 255)
    private String app;

    @NotBlank(message = "Не указан URI")
    @Size(max = 512)
    private String uri;

    @NotBlank(message = "Не указан IP пользователя")
    @Size(max = 255)
    private String ip;

    @NotNull(message = "Не задано время отправления запроса")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
