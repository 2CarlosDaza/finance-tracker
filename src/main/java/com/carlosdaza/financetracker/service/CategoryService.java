package com.carlosdaza.financetracker.service;

import com.carlosdaza.financetracker.domain.dto.FinanceDto;
import com.carlosdaza.financetracker.domain.entity.Category;
import com.carlosdaza.financetracker.domain.enums.TransactionType;
import com.carlosdaza.financetracker.exception.BusinessException;
import com.carlosdaza.financetracker.exception.ResourceNotFoundException;
import com.carlosdaza.financetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public FinanceDto.CategoryResponse create(FinanceDto.CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new BusinessException("Category already exists: " + request.name());
        }
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .type(request.type())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<FinanceDto.CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FinanceDto.CategoryResponse> getByType(TransactionType type) {
        return categoryRepository.findByType(type).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FinanceDto.CategoryResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public FinanceDto.CategoryResponse update(UUID id, FinanceDto.CategoryRequest request) {
        Category category = findOrThrow(id);
        if (!category.getName().equals(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new BusinessException("Category name already in use: " + request.name());
        }
        category.setName(request.name());
        category.setDescription(request.description());
        category.setType(request.type());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        findOrThrow(id);
        categoryRepository.deleteById(id);
    }

    public Category findOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));
    }

    private FinanceDto.CategoryResponse toResponse(Category c) {
        return FinanceDto.CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .type(c.getType())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
