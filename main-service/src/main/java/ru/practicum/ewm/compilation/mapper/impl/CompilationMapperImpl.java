package ru.practicum.ewm.compilation.mapper.impl;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Service
public class CompilationMapperImpl implements CompilationMapper {
    @Override
    public Compilation mapToCompilation(CompilationDto compilationDto, List<Event> events) {
        Compilation compilation = Compilation.builder()
                .title(compilationDto.getTitle())
                .events(events)
                .build();

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }

        if (compilationDto.getId() != null) {
            compilation.setId(compilationDto.getId());
        }

        return compilation;
    }

    @Override
    public Compilation mapToCompilation(NewCompilationDto compilationDto, List<Event> events) {
        Compilation compilation = Compilation.builder()
                .title(compilationDto.getTitle())
                .events(events)
                .build();

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }

        return compilation;
    }

    @Override
    public CompilationDto mapToCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(events)
                .build();
    }
}
