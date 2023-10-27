package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.SortEvents;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  SortEvents sort,
                                  Integer from,
                                  Integer size,
                                  HttpServletRequest request);

    EventDto getEventById(Long eventId, HttpServletRequest request);

    List<EventDto> getAllEventsByUserId(Long userId, Integer from, Integer size);

    EventDto getEventByUserId(Long userId, Long eventId);

    EventDto updateEventsByUser(Long userId, Long eventId, UpdateEventDto updateEventDto);

    List<RequestDto> getRequestUserEvents(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(Long userId, Long eventId, EventRequestStatusUpdateRequest requestDto);

    List<EventDto> adminGetEvents(List<Long> userIds,
                                  List<State> states,
                                  List<Long> categories,
                                  String rangeStart,
                                  String rangeEnd,
                                  Integer from,
                                  Integer size);

    EventDto adminUpdateEvent(Long eventId, UpdateEventDto updateEventDto);
}
