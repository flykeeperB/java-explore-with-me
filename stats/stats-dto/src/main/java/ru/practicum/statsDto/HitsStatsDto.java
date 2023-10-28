package ru.practicum.statsDto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HitsStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
