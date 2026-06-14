package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.EventDto;
import com.baruunaylal.backend.service.AuditLogService;
import com.baruunaylal.backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final AuditLogService auditLogService;

    @GetMapping
    public List<EventDto> getAllEvents() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SYSTEM_ADMIN', 'ROLE_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    @PostMapping
    public EventDto createEvent(@RequestBody EventDto dto, Principal principal) {
        EventDto created = eventService.create(dto);
        auditLogService.record(principal.getName(), "Event Create", "Created event: " + created.getTitle());
        return created;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SYSTEM_ADMIN', 'ROLE_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public EventDto updateEvent(@PathVariable Long id, @RequestBody EventDto dto, Principal principal) {
        EventDto updated = eventService.update(id, dto);
        auditLogService.record(principal.getName(), "Event Update", "Updated event: " + updated.getTitle());
        return updated;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SYSTEM_ADMIN', 'ROLE_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id, Principal principal) {
        eventService.delete(id);
        auditLogService.record(principal.getName(), "Event Delete", "Deleted event ID: " + id);
    }
}
