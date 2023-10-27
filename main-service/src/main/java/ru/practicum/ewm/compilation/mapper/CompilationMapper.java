package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public interface CompilationMapper {

    Compilation mapToCompilation(CompilationDto compilationDto, List<Event> events);

    Compilation mapToCompilation(NewCompilationDto compilationDto, List<Event> events);

    CompilationDto mapToCompilationDto(Compilation compilation, List<EventShortDto> events);

}
