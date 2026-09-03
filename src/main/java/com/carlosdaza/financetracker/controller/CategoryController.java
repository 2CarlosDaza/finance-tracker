package com.carlosdaza.financetracker.controller;

import com.carlosdaza.financetracker.domain.dto.FinanceDto;
import com.carlosdaza.financetracker.domain.enums.TransactionType;
import com.carlosdaza.financetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Manage income and expense categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<FinanceDto.CategoryResponse> create(
            @Valid @RequestBody FinanceDto.CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @GetMapping
    @Operation(summary = "List all categories, optionally filtered by type")
    public ResponseEntity<List<FinanceDto.CategoryResponse>> getAll(
            @RequestParam(required = false) TransactionType type) {
        return ResponseEntity.ok(type != null
                ? categoryService.getByType(type)
                : categoryService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<FinanceDto.CategoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getById(id));
        
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<FinanceDto.CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody FinanceDto.CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
