package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.EventDto;
import com.baruunaylal.backend.entity.Event;
import com.baruunaylal.backend.exception.ResourceNotFoundException;
import com.baruunaylal.backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<EventDto> findAll() {
        return eventRepository.findAllByOrderByEventDateAscEventTimeAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDto findById(Long id) {
        return eventRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    @Transactional
    public EventDto create(EventDto dto) {
        Event event = new Event();
        applyDto(event, dto);
        return toDto(eventRepository.save(event));
    }

    @Transactional
    public EventDto update(Long id, EventDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
        applyDto(event, dto);
        return toDto(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found: " + id);
        }
        eventRepository.deleteById(id);
    }

    private void applyDto(Event event, EventDto dto) {
        event.setTitle(dto.getTitle());
        event.setType(dto.getType());
        event.setEventDate(LocalDate.parse(dto.getDate()));
        event.setEventTime(LocalTime.parse(dto.getTime()));
        event.setSoum(dto.getSoum());
        event.setLocation(dto.getLocation());
        event.setOrganizer(dto.getOrganizer());
        event.setImage(dto.getImage());
        event.setMapUrl(dto.getMapUrl());
        event.setDescription(dto.getDescription());
        event.setDetails(dto.getDetails());
    }

    private EventDto toDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getType(),
                event.getEventDate() != null ? event.getEventDate().toString() : null,
                event.getEventTime() != null ? event.getEventTime().toString() : null,
                event.getSoum(),
                event.getLocation(),
                event.getOrganizer(),
                event.getImage(),
                event.getMapUrl(),
                event.getDescription(),
                event.getDetails()
        );
    }
}
