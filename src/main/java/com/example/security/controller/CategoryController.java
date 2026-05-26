package com.example.security.controller;

import com.example.security.dto.ApiResponse;
import com.example.security.dto.request.CategoryRequest;
import com.example.security.dto.response.CategoryResponse;
import com.example.security.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAllActive() {
        return ApiResponse.ok("Categorias ativas", categoryService.findAllActive());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAll() {
        return ApiResponse.ok("Todas as categorias", categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable Long id) {
        return ApiResponse.ok("Categoria encontrada", categoryService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.created("Categoria criada com sucesso", categoryService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.ok("Categoria atualizada", categoryService.update(id, request));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        categoryService.toggleActive(id);
        return ApiResponse.noContent("Status da categoria alterado");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.noContent("Categoria removida com sucesso");
    }
}
