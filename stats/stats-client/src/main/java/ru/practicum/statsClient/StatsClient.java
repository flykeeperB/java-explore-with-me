package ru.practicum.statsClient;

import ru.practicum.statsDto.HitsStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void addHit(String app, String uri, String ip, LocalDateTime dateTime);

    List<HitsStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
