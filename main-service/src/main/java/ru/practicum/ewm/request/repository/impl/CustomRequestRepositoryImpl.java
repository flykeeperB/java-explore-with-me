package ru.practicum.ewm.request.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.CustomRequestRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomRequestRepositoryImpl implements CustomRequestRepository {

    private final EntityManager entityManager;

    @Override
    public Map<Long, Long> countAllByEventIdAndStatus(List<Long> eventsIds, RequestStatus state) {
        Map<Long, Long> resultMap = new HashMap<>();

        for (Long eventsId : eventsIds) {
            resultMap.put(eventsId, 0L);
        }

        String jpql = "SELECT r.event.id, COUNT(*) " +
                "FROM Request as r " +
                "WHERE r.event.id in (:ids) and r.status = :status " +
                "GROUP BY r.event.id";

        Query query = entityManager.createQuery(jpql);

        query.setParameter("ids", eventsIds);
        query.setParameter("status", state);

        List<Object[]> results = query.getResultList();

        for (Object[] row : results) {
            resultMap.put((Long) row[0], (Long) row[1]);
        }

        return resultMap;
    }
}
