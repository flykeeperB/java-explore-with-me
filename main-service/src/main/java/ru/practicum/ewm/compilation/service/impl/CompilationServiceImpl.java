package ru.practicum.ewm.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        log.info("createCompilation " + compilationDto);

        List<Event> events = new ArrayList<>();
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findAllById(compilationDto.getEvents());
        }
        Compilation compilation = compilationMapper.mapToCompilation(compilationDto, events);
        Compilation result = compilationRepository.save(compilation);

        return compilationMapper.mapToCompilationDto(result, eventMapper.mapToEventShortDto(events));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("getCompilations");

        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }
        if (compilations.isEmpty()) {
            return Collections.emptyList();
        }

        return compilations.stream().map(compilation ->
                        compilationMapper.mapToCompilationDto(compilation,
                                eventMapper.mapToEventShortDto(compilation.getEvents())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compilationId) {

        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Запрошеной подборки не существует!"));

        return compilationMapper.mapToCompilationDto(compilation,
                eventMapper.mapToEventShortDto(compilation.getEvents()));
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        log.info(String.format("deleteCompilation id-%d", compilationId));

        compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Такой подборки не существует!"));

        compilationRepository.deleteById(compilationId);
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilationDto) {
        log.info(String.format("updateCompilation id-%d", compilationId));

        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Запрошеной для обновления подборки не существует!"));

        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(compilationDto.getEvents()));
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        Compilation result = compilationRepository.save(compilation);

        return compilationMapper.mapToCompilationDto(result, eventMapper.mapToEventShortDto(result.getEvents()));
    }
}
