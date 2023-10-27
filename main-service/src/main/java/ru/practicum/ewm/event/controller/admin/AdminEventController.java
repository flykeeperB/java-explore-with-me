package ru.practicum.ewm.event.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventDto> adminGetEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<State> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {

        return eventService.adminGetEvents(users, states, categories, rangeStart, rangeEnd, from, size);

    }

    @PatchMapping("/{eventId}")
    public EventDto patchAdminEvent(@PathVariable @Min(1) Long eventId,
                                    @RequestBody @Validated UpdateEventDto updateEventDto) {

        return eventService.adminUpdateEvent(eventId, updateEventDto);

    }
}
