package ru.practicum.ewm.request.mapper.impl;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestMapperImpl implements RequestMapper {

    @Override
    public RequestDto mapToRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .build();
    }

    @Override
    public Request mapToRequest(RequestDto requestDto) {
        Request result = Request.builder()
                .created(requestDto.getCreated())
                .status(requestDto.getStatus())
                .build();
        if (requestDto.getId() != null) {
            result.setId(requestDto.getId());
        }
        return result;
    }

    @Override
    public List<RequestDto> mapToRequestDto(List<Request> requests) {
        return requests.stream().map(this::mapToRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult mapToUpdateResultDto(List<Request> confirmedRequests, List<Request> rejectedRequests) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream().map(this::mapToRequestDto).collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream().map(this::mapToRequestDto).collect(Collectors.toList()))
                .build();
    }
}
