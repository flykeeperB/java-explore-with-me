package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long id);

    List<Request> findByEventId(Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, RequestStatus state);

    List<Request> findAllByIdIn(List<Long> ids);

    boolean existsRequestByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

}
