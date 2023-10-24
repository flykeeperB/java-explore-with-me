package ru.practicum.mapper.impl;

import org.springframework.stereotype.Service;
import ru.practicum.statsDto.HitsStatsDto;
import ru.practicum.mapper.HitStatsMapper;
import ru.practicum.model.HitsStats;

@Service
public class HitStatsMapperImpl implements HitStatsMapper {
    @Override
    public HitsStatsDto toHitsStatsDto(HitsStats hitsStats) {
        return HitsStatsDto.builder()
                .app(hitsStats.getApp())
                .uri(hitsStats.getUri())
                .hits(hitsStats.getHits())
                .build();
    }
}
