package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestMapper {

    Request mapToRequest(RequestDto requestDto);

    RequestDto mapToRequestDto(Request request);

    List<RequestDto> mapToRequestDto(List<Request> requests);

    EventRequestStatusUpdateResult mapToUpdateResultDto(
            List<Request> confirmedRequests,
            List<Request> rejectedRequests
    );

}
