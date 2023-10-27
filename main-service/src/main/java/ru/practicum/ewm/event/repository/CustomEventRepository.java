package ru.practicum.ewm.event.repository;

import ru.practicum.ewm.event.dto.Criteria;
import ru.practicum.ewm.event.dto.PublicCriteria;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public interface CustomEventRepository {

    List<Event> getEvents(Criteria criteria);

    List<Event> findEventsPublic(PublicCriteria criteriaPublic);

}
