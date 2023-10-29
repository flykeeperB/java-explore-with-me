package ru.practicum.statsClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsDto.HitDto;
import ru.practicum.statsDto.HitsStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StatsClientImpl implements StatsClient {

    private final String serverUrl;

    private final RestTemplate rest = new RestTemplate();

    private static final String HIT_ENDPOINT = "/hit";
    private static final String GET_STATS_ENDPOINT = "/stats";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClientImpl(@Value("${stats-server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }


    @Override
    public void addHit(String app, String uri, String ip, LocalDateTime dateTime) {
        HitDto body = new HitDto(app, uri, ip, dateTime);

        rest.postForLocation(serverUrl.concat(HIT_ENDPOINT), body);
    }

    @Override
    public List<HitsStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "unique", unique));

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
        }

        HitsStatsDto[] response = rest.getForObject(
                serverUrl.concat(GET_STATS_ENDPOINT + "?start={start}&end={end}&uris={uris}&unique={unique}"),
                HitsStatsDto[].class,
                parameters);

        return Objects.isNull(response) ? List.of() : List.of(response);
    }
}