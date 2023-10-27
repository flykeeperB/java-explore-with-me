package ru.practicum.ewm.event.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.SortEvents;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.IncorrectRequestException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false, defaultValue = "EVENT_DATE") SortEvents sort,
                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @RequestParam(required = false, defaultValue = "10") Integer size, HttpServletRequest request) {

        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }

        if (start != null && end != null) {
            if (start.isAfter(end)) {
                log.info("Start date {} is after end date {}.", start, end);
                throw new IncorrectRequestException(String.format("Start date %s is after end date %s.", start, end));
            }
        }

        return eventService.getEvents(text, categories, paid, start, end, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable Long id, HttpServletRequest request) {

        return eventService.getEventById(id, request);
    }


}
