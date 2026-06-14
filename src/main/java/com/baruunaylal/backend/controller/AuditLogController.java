package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.entity.AuditLog;
import com.baruunaylal.backend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditLogService.findAll();
    }
}
