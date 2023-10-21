package ru.practicum.mapper;

import ru.practicum.statsDto.HitDto;
import ru.practicum.model.Hit;

public interface HitMapper {

    public Hit toHit(HitDto hitDto);

    public HitDto toHitDto(Hit hit);

}
