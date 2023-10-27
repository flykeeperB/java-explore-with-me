package ru.practicum.ewm.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        log.info(String.format("createRequest id-%d eventId-%d", userId, eventId));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие на найдено."));

        Long confirmedRequestAmount = requestsRepository.countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в собственном событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Участвовать в неопубликованном событии нельзя.");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequestAmount) {
            throw new ConflictException("Достигнут лимит запросов на участие.");
        }
        if (requestsRepository.existsRequestByRequester_IdAndEvent_Id(userId, eventId)) {
            throw new ConflictException("Нельзя создать запрос повторно");
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) { // нужна ли модерация на участие
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return requestMapper.mapToRequestDto(requestsRepository.save(request));
    }

    @Override
    public List<RequestDto> getRequest(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        List<Request> requestsList = requestsRepository.findByRequesterId(userId);

        return requestMapper.mapToRequestDto(requestsList);
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.info(String.format("cancelRequest id-%d requestId-%d", userId, requestId));

        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        Request request = requestsRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос не найден."));

        if (!userId.equals(request.getRequester().getId())) {
            throw new ConflictException("Нельзя отменить чужой запрос на участие.");
        }

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.mapToRequestDto(requestsRepository.save(request));
    }
}
