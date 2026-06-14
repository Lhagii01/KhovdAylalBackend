package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.CategoryDto;
import com.baruunaylal.backend.service.AuditLogService;
import com.baruunaylal.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final AuditLogService auditLogService;

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto dto, Principal principal) {
        CategoryDto created = categoryService.create(dto);
        auditLogService.record(principal.getName(), "Category Create", "Created category: " + created.getName());
        return created;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id, @RequestBody CategoryDto dto, Principal principal) {
        CategoryDto updated = categoryService.update(id, dto);
        auditLogService.record(principal.getName(), "Category Update", "Updated category: " + updated.getName());
        return updated;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id, Principal principal) {
        categoryService.delete(id);
        auditLogService.record(principal.getName(), "Category Delete", "Deleted category ID: " + id);
    }
}
