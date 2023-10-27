package ru.practicum.ewm.event.mapper.impl;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventMapperImpl implements EventMapper {

    @Override
    public EventDto mapToEventDto(Event event) {
        EventDto eventDto = EventDto.builder()
                .annotation(event.getAnnotation())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .location(new LocationDto(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .build();

        if (event.getParticipationRequests() != null && !event.getParticipationRequests().isEmpty()) {
            eventDto.setConfirmedRequests(event.getParticipationRequests().stream()
                    .filter(participationRequest -> participationRequest.getStatus() == RequestStatus.CONFIRMED)
                    .count());
        } else eventDto.setConfirmedRequests(0L);

        return eventDto;
    }

    @Override
    public EventDto mapToEventDto(Event event, CategoryDto categoryDto, UserDto userDto) {
        EventDto eventDto = this.mapToEventDto(event);
        eventDto.setCategory(categoryDto);
        eventDto.setInitiator(userDto);
        return eventDto;
    }

    @Override
    public EventShortDto mapToEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getParticipantLimit())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    @Override
    public EventShortDto mapToEventShortDto(Event event, CategoryDto categoryDto, UserDto userDto) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getParticipantLimit())
                .eventDate(event.getEventDate())
                .initiator(userDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    @Override
    public Event mapToEvent(EventDto eventDto, Category category, User user) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(category)
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .lat(eventDto.getLocation().getLat())
                .lon(eventDto.getLocation().getLon())
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .title(eventDto.getTitle())
                .initiator(user)
                .state(State.PENDING)
                .build();
    }

    @Override
    public Event mapToEvent(NewEventDto newEventDto, Category category, User user) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .lon(newEventDto.getLocation().getLon())
                .lat(newEventDto.getLocation().getLat())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .initiator(user)
                .state(State.PENDING)
                .build();
    }

    @Override
    public Event mapToEvent(UpdateEventDto updateEventDto, Category category) {
        Event result = Event.builder()
                .annotation(updateEventDto.getAnnotation())
                .category(category)
                .description(updateEventDto.getDescription())
                .eventDate(updateEventDto.getEventDate())

                .paid(updateEventDto.getPaid())
                .participantLimit(updateEventDto.getParticipantLimit())
                .requestModeration(updateEventDto.getRequestModeration())
                .title(updateEventDto.getTitle())
                .build();

        if (updateEventDto.getLocation() != null) {
            result.setLat(updateEventDto.getLocation().getLat());
            result.setLon(updateEventDto.getLocation().getLon());
        }

        return result;
    }

    @Override
    public List<EventDto> mapToEventDto(List<Event> events) {
        return events.stream().map(this::mapToEventDto).collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> mapToEventShortDto(List<Event> events) {
        return events.stream().map(this::mapToEventShortDto).collect(Collectors.toList());
    }
}
