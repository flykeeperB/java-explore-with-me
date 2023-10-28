package ru.practicum.ewm.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.SortEvents;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.repository.CustomEventRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.IncorrectRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.CustomRequestRepository;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.stats.StatsHelper;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StatsHelper statsHelper;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final CustomRequestRepository customRequestRepository;
    private final CustomEventRepository customEventRepository;

    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final RequestMapper requestMapper;
    private final UserMapper userMapper;

    @Override
    public EventDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info(String.format("createEvent userId-%d eventDto-%s", userId, newEventDto));

        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }

        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Cобытие не может быть раньше, чем через два часа от текущего момента!");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        Category category = getCategory(newEventDto.getCategory());

        Event event = eventMapper.mapToEvent(newEventDto, category, user);
        event.setConfirmedRequests(0L);
        event.setViews(0L);
        event.setCreatedOn(LocalDateTime.now());

        Event createdEvent = eventRepository.save(event);

        return toDto(createdEvent);
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         SortEvents sort,
                                         Integer from,
                                         Integer size,
                                         String ip,
                                         String uri) {

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IncorrectRequestException(String.format("Дата начала %s позже даты завершения %s.",
                        rangeStart, rangeEnd));
            }
        }

        PublicCriteria criteria = PublicCriteria.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        List<Event> events = customEventRepository.findEventsPublic(criteria);

        List<EventShortDto> result = events.stream().map(this::toShortDto).collect(Collectors.toList());

        if (result.size() > 0) {
            statsHelper.setViewsNumberForShortDto(result);

            Map<Long, Long> confirmedRequests = customRequestRepository
                    .countAllByEventIdAndStatus(
                            result.stream().map(EventShortDto::getId).collect(Collectors.toList()),
                            RequestStatus.CONFIRMED
                    );

            for (EventShortDto event : result) {
                event.setConfirmedRequests(confirmedRequests.get(event.getId()));
            }

        }

        statsHelper.saveHit(uri, ip);

        if (result.size() > 0) {
            for (EventShortDto event : result) {
                statsHelper.saveHit("/events/" + event.getId(), ip);
            }
        } else {
            return new ArrayList<>();
        }
        if (criteria.getSort() == SortEvents.VIEWS) {
            return result.stream().sorted(Comparator.comparingLong(EventShortDto::getViews)).collect(Collectors.toList());
        }

        return result.stream().sorted(Comparator.comparing(EventShortDto::getEventDate)).collect(Collectors.toList());
    }

    @Override
    public EventDto getEventById(Long eventId, String ip) {
        Event event = eventRepository.findByIdAndAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        EventDto eventDto = toDto(event);
        statsHelper.saveHit("/events/" + eventId, ip);
        statsHelper.setViewsNumber(eventDto);
        eventDto.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
                RequestStatus.CONFIRMED));

        return eventDto;
    }

    @Override
    public List<EventDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);

        List<Event> events = eventRepository.findByInitiatorId(userId, page);

        return eventMapper.mapToEventDto(events);
    }

    @Override
    public EventDto getEventByUserId(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new NotFoundException("Cобытие не найдено."));

        return eventMapper.mapToEventDto(event);
    }

    @Override
    public EventDto updateEventsByUser(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Cобытие не найдено."));

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации!");
        }

        Category category = getCategory(updateEventDto.getCategory());

        Event updateData = eventMapper.mapToEvent(updateEventDto, category);

        patchEvents(event, updateData);

        if (updateEventDto.getStateAction() != null) {

            switch (updateEventDto.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    event.setPublishedOn(LocalDateTime.now());
            }
        }

        Event updatedEvent = eventRepository.save(event);

        EventDto result = toDto(updatedEvent);
        result.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
                RequestStatus.CONFIRMED));

        statsHelper.setViewsNumber(result);

        return result;
    }

    @Override
    public List<RequestDto> getRequestUserEvents(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Cобытие не найдено."));

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не инициатор события!");
        }

        List<Request> requests = requestRepository.findByEventId(eventId);

        return requestMapper.mapToRequestDto(requests);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(Long userId,
                                                                               Long eventId,
                                                                               EventRequestStatusUpdateRequest requestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Cобытие не найдено."));

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не инициатор события.");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Не требуется модерация и подтверждения заявок.");
        }

        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (RequestStatus.valueOf(requestDto.getStatus()) == RequestStatus.REJECTED && confirmedRequests > 0) {
            throw new ConflictException("Нельзя отменить принятую заявку.");
        }

        if (RequestStatus.valueOf(requestDto.getStatus()) == RequestStatus.CONFIRMED) {
            if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= (confirmedRequests)) {
                throw new ConflictException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие.");
            }
        }

        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(requestDto.getRequestIds());
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        for (Request request : requestsToUpdate) {

            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                continue;
            }

            if (!request.getEvent().getId().equals(eventId)) {
                rejected.add(request);
                continue;
            }

            if (RequestStatus.valueOf(requestDto.getStatus()) == RequestStatus.CONFIRMED) {
                if (confirmedRequests < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests++;
                    confirmed.add(request);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejected.add(request);
                }

            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejected.add(request);
            }
        }

        requestRepository.saveAll(requestsToUpdate);

        return requestMapper.mapToUpdateResultDto(confirmed, rejected);
    }

    @Override
    public List<EventDto> adminGetEvents(List<Long> userIds,
                                         List<State> states,
                                         List<Long> categories,
                                         String rangeStart,
                                         String rangeEnd,
                                         Integer from,
                                         Integer size) {

        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new IncorrectRequestException("Дата начала  не может быть после даты завершения.");
            }
        }
        Criteria criteria = Criteria.builder()
                .users(userIds)
                .states(states)
                .categories(categories)
                .from(from)
                .size(size)
                .rangeStart(start)
                .rangeEnd(end)
                .build();
        List<Event> events = customEventRepository.getEvents(criteria);

        List<EventDto> result = events.stream().map(this::toDto)
                .collect(Collectors.toList());

        statsHelper.setViewsNumber(result);

        return result;
    }

    @Override
    public EventDto adminUpdateEvent(Long eventId, UpdateEventDto updateEventDto) {
        log.info(String.format("adminUpdateEvent eventId-%d, updateEventDto-%s", eventId, updateEventDto));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Cобытие не найдено."));

        if (updateEventDto.getEventDate() != null
                && event.getPublishedOn() != null
                && updateEventDto.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new IncorrectRequestException("дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }

        Category category = getCategory(updateEventDto.getCategory());

        Event updateData = eventMapper.mapToEvent(updateEventDto, category);

        if (updateEventDto.getStateAction() != null) {

            switch (updateEventDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != State.PENDING) {
                        throw new ConflictException("Состояние события должно быть PENDING");
                    }
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState() == State.PUBLISHED) {
                        throw new ConflictException("Невозможно отменить опубликованное мероприятие");
                    }
                    event.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                case CANCEL_REVIEW:
                    if (event.getState() == State.PUBLISHED) {
                        throw new ConflictException("Состояние события должно быть на ожидании или отмененным");
                    }
                    break;
            }
        }

        patchEvents(event, updateData);

        Event updatedEvent = eventRepository.save(event);
        EventDto updatedEventDto = toDto(updatedEvent);
        updatedEventDto.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
                RequestStatus.CONFIRMED));

        statsHelper.setViewsNumber(updatedEventDto);

        return updatedEventDto;
    }

    private void patchEvents(Event targetEvent, Event sourceEvent) {
        if (sourceEvent.getAnnotation() != null) {
            targetEvent.setAnnotation(sourceEvent.getAnnotation());
        }
        if (sourceEvent.getCategory() != null) {
            targetEvent.setCategory(sourceEvent.getCategory());
        }
        if (sourceEvent.getDescription() != null) {
            targetEvent.setDescription(sourceEvent.getDescription());
        }
        if (sourceEvent.getEventDate() != null) {
            targetEvent.setEventDate(sourceEvent.getEventDate());
        }
        if (sourceEvent.getLat() != null) {
            targetEvent.setLat(sourceEvent.getLat());
        }
        if (sourceEvent.getLon() != null) {
            targetEvent.setLon(sourceEvent.getLon());
        }
        if (sourceEvent.getPaid() != null) {
            targetEvent.setPaid(sourceEvent.getPaid());
        }
        if (sourceEvent.getParticipantLimit() != null) {
            targetEvent.setParticipantLimit(sourceEvent.getParticipantLimit());
        }
        if (sourceEvent.getRequestModeration() != null) {
            targetEvent.setRequestModeration(sourceEvent.getRequestModeration());
        }
        if (sourceEvent.getTitle() != null) {
            targetEvent.setTitle(sourceEvent.getTitle());
        }
    }

    private Category getCategory(Long categoryId) {
        if (categoryId != null) {
            return categoryRepository.findById(categoryId).orElseThrow(
                    () -> new NotFoundException("Категория не найдена.")
            );
        }

        return null;
    }

    private EventDto toDto(Event event) {
        CategoryDto categoryDto = categoryMapper.mapToCategoryDto(event.getCategory());
        UserDto initiatorDto = userMapper.mapToUserDto(event.getInitiator());

        return eventMapper.mapToEventDto(event, categoryDto, initiatorDto);
    }

    private EventShortDto toShortDto(Event event) {
        CategoryDto categoryDto = categoryMapper.mapToCategoryDto(event.getCategory());
        UserDto initiatorDto = userMapper.mapToUserDto(event.getInitiator());

        return eventMapper.mapToEventShortDto(event, categoryDto, initiatorDto);
    }
}
