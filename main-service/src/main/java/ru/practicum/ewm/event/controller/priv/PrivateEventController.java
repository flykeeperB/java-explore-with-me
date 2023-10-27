package ru.practicum.ewm.event.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventDto> getAllEventsByUserId(@PathVariable Long userId,
                                               @RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "10")
                                               @PositiveOrZero Integer size) {

        return eventService.getAllEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvents(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventsByUserId(@PathVariable Long userId,
                                      @PathVariable Long eventId) {
        return eventService.getEventByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventsByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @RequestBody @Valid UpdateEventDto updateEventDto) {
        return eventService.updateEventsByUser(userId, eventId, updateEventDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestUserEvents(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        return eventService.getRequestUserEvents(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(@PathVariable Long userId,
                                                                               @PathVariable Long eventId,
                                                                               @RequestBody @Valid EventRequestStatusUpdateRequest requestDto) {

        return eventService.updateStatusRequestByUserIdForEvents(userId, eventId, requestDto);
    }
}
