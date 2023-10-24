package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ValidationException;
import ru.practicum.statsDto.HitDto;
import ru.practicum.statsDto.HitsStatsDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.mapper.HitStatsMapper;
import ru.practicum.model.HitsStats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitMapper hitMapper;
    private final HitStatsMapper hitStatsMapper;
    private final StatsRepository statsRepository;

    @Override
    public void addHit(HitDto hitDto) {
        statsRepository.save(hitMapper.toHit(hitDto));
    }

    @Override
    public List<HitsStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<HitsStats> stats;

        if (start.isAfter(end)) {
            throw new ValidationException("Время начала диапазона позднее даты конца диапазона!");
        }

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                stats = statsRepository.findAllByDateBetweenUnique(start, end);
            } else {
                stats = statsRepository.findAllByDateBetween(start, end);
            }
        } else {
            if (unique) {
                stats = statsRepository.findAllByDateBetweenUnique(start, end, uris);
            } else {
                stats = statsRepository.findAllByDateBetween(start, end, uris);
            }

        }

        return stats.stream()
                .map(hitStatsMapper::toHitsStatsDto)
                .collect(Collectors.toList());
    }
}
