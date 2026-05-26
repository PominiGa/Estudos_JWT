package com.example.security.service;

import com.example.security.dto.request.CategoryRequest;
import com.example.security.dto.response.CategoryResponse;
import com.example.security.entity.Category;
import com.example.security.exception.CategoryNotFoundException;
import com.example.security.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public List<CategoryResponse> findAllActive() {
        return categoryRepository.findByActive(true)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryResponse::from)
                .orElseThrow(CategoryNotFoundException::new);
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Categoria com esse nome já existe");
        }
        Category category = Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .active(true)
                .build();
        Category saved = categoryRepository.save(category);
        log.info("Categoria criada: id={}, name={}", saved.getId(), saved.getName());
        return CategoryResponse.from(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        categoryRepository.findByName(request.getName())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new IllegalArgumentException("Já existe uma categoria com esse nome"); });

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        Category saved = categoryRepository.save(category);
        log.info("Categoria atualizada: id={}", id);
        return CategoryResponse.from(saved);
    }

    public void toggleActive(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        category.setActive(!category.isActive());
        categoryRepository.save(category);
        log.info("Categoria id={} status alterado para active={}", id, category.isActive());
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(category);
        log.info("Categoria deletada: id={}", id);
    }
}