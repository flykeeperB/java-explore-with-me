package ru.practicum.mapper;

import ru.practicum.statsDto.HitsStatsDto;
import ru.practicum.model.HitsStats;

public interface HitStatsMapper {

    public HitsStatsDto toHitsStatsDto(HitsStats hitsStats);

}
