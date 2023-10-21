package ru.practicum.mapper.impl;

import org.springframework.stereotype.Service;
import ru.practicum.statsDto.HitDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;

@Service
public class HitMapperImpl implements HitMapper {
    @Override
    public Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .ip(hitDto.getIp())
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .timestamp(hitDto.getTimestamp())
                .build();
    }

    @Override
    public HitDto toHitDto(Hit hit) {
        return HitDto.builder()
                .ip(hit.getIp())
                .app(hit.getApp())
                .uri(hit.getUri())
                .timestamp(hit.getTimestamp())
                .build();
    }
}
