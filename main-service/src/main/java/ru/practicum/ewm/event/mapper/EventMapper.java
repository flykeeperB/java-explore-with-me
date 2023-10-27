package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface EventMapper {

    EventDto mapToEventDto(Event event);

    EventDto mapToEventDto(Event event, CategoryDto categoryDto, UserDto userDto);

    EventShortDto mapToEventShortDto(Event event);

    EventShortDto mapToEventShortDto(Event event,
                                     CategoryDto categoryDto,
                                     UserDto userDto);

    Event mapToEvent(EventDto eventDto, Category category, User user);

    Event mapToEvent(NewEventDto newEventDto, Category category, User user);

    Event mapToEvent(UpdateEventDto updateEventDto, Category category);

    List<EventDto> mapToEventDto(List<Event> events);

    List<EventShortDto> mapToEventShortDto(List<Event> events);

}
