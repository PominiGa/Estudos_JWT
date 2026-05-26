package com.example.security.controller;

import com.example.security.dto.ApiResponse;
import com.example.security.dto.request.ProductRequest;
import com.example.security.dto.response.ProductResponse;
import com.example.security.entity.User;
import com.example.security.service.ProductsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAll(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok("Produtos encontrados", productsService.getAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ApiResponse.ok("Resultado da busca", productsService.search(name, category, minPrice, maxPrice, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ApiResponse.ok("Produto encontrado", productsService.getById(id));
    }

    @GetMapping("/ean")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> getByEan(
            @RequestParam String ean,
            @AuthenticationPrincipal User user) {
        return ApiResponse.ok("Produto encontrado", productsService.findByEan(user, ean));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProductRequest req) {
        return ApiResponse.created("Produto criado com sucesso", productsService.createProduct(user, req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> editById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest req) {
        return ApiResponse.ok("Produto atualizado", productsService.editById(user, id, req));
    }

    @PutMapping("/by-name")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> editByName(
            @AuthenticationPrincipal User user,
            @RequestParam String name,
            @Valid @RequestBody ProductRequest req) {
        return ApiResponse.ok("Produto atualizado", productsService.editByName(user, name, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        productsService.deleteById(user, id);
        return ApiResponse.noContent("Produto removido com sucesso");
    }

    @DeleteMapping("/by-name")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteByName(
            @AuthenticationPrincipal User user,
            @RequestParam String name) {
        productsService.deleteByName(user, name);
        return ApiResponse.noContent("Produto removido com sucesso");
    }
}
