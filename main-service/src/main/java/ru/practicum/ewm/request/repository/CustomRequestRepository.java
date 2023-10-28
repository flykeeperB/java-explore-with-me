package ru.practicum.ewm.request.repository;

import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;
import java.util.Map;

public interface CustomRequestRepository {
    Map<Long, Long> countAllByEventIdAndStatus(List<Long> eventsIds, RequestStatus state);
}
