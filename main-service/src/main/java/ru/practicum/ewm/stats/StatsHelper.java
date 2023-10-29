package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.statsClient.StatsClient;
import ru.practicum.statsDto.HitsStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsHelper {
    private static final String APP = "main-service";
    private static final int YEARS_OFFSET = 100;
    private final StatsClient statsClient;

    public void saveHit(String uri, String ip) {
        statsClient.addHit(APP, uri, ip, LocalDateTime.now());
    }

    public EventDto setViewsNumber(EventDto event) {
        List<HitsStatsDto> hits = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(),
                List.of("/events/" + event.getId()), true);
        if (!hits.isEmpty()) {
            event.setViews(hits.get(0).getHits());
        } else {
            event.setViews(0L);
        }
        return event;
    }

    public List<EventDto> setViewsNumber(List<EventDto> events) {
        List<String> uris = new ArrayList<>();
        for (EventDto eventDto : events) {
            uris.add("/events/" + eventDto.getId());
        }

        List<HitsStatsDto> hits = statsClient.getStats(LocalDateTime.now().minusYears(YEARS_OFFSET),
                LocalDateTime.now(), uris, true);
        if (!hits.isEmpty()) {
            Map<Long, Long> hitMap = mapHits(hits);
            for (EventDto event : events) {
                event.setViews(hitMap.getOrDefault(event.getId(), 0L));
            }
        } else {
            for (EventDto event : events) {
                event.setViews(0L);
            }
        }
        return events;
    }

    public List<EventShortDto> setViewsNumberForShortDto(List<EventShortDto> events) {
        List<String> uris = new ArrayList<>();
        for (EventShortDto eventShortDto : events) {
            uris.add("/events/" + eventShortDto.getId());
        }

        List<HitsStatsDto> hits = statsClient.getStats(LocalDateTime.now().minusYears(YEARS_OFFSET),
                LocalDateTime.now(), uris, true);
        if (!hits.isEmpty()) {
            Map<Long, Long> hitMap = mapHits(hits);
            for (EventShortDto event : events) {
                event.setViews(hitMap.getOrDefault(event.getId(), 0L));
            }
        } else {
            for (EventShortDto event : events) {
                event.setViews(0L);
            }
        }
        return events;
    }

    private Map<Long, Long> mapHits(List<HitsStatsDto> hits) {
        Map<Long, Long> hitMap = new HashMap<>();
        for (var hit : hits) {
            String hitUri = hit.getUri();
            Long id = Long.valueOf(hitUri.substring(8));
            hitMap.put(id, (Long) hit.getHits());
        }
        return hitMap;
    }
}
