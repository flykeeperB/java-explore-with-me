package ru.practicum.service;

import ru.practicum.statsDto.HitDto;
import ru.practicum.statsDto.HitsStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void addHit (HitDto hitDto);

    List<HitsStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
