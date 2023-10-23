package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatsService;
import ru.practicum.statsDto.HitDto;
import ru.practicum.statsDto.HitsStatsDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postEndpointHit(@Valid @RequestBody HitDto hitDto) {

        statisticService.addHit(hitDto);

    }

    @GetMapping("/stats")
    public List<HitsStatsDto> getStatistic(@RequestParam("start")
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam("end")
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(name = "uris", required = false, defaultValue = "")
                                           List<String> uris,
                                           @RequestParam(name = "unique", required = false, defaultValue = "false")
                                           Boolean unique) {

        return statisticService.getStats(start, end, uris, unique);

    }

}
