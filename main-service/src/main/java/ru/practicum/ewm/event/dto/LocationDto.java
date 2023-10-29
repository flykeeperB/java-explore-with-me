package ru.practicum.ewm.event.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LocationDto {

    @NotNull
    private float lat;

    @NotNull
    private float lon;
}
