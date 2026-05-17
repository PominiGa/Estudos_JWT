package com.example.security.controller;

import com.example.security.dto.request.ProductRequest;
import com.example.security.entity.Products;
import com.example.security.entity.User;
import com.example.security.service.ProductsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping
    public ResponseEntity<List<Products>> getAll() {
        return ResponseEntity.ok(productsService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Products> getById(@PathVariable long id) {
        return ResponseEntity.ok(productsService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Products>> getByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(productsService.getByName(name));
    }

    @PostMapping
    public ResponseEntity<Products> createProduct(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.status(201).body(productsService.createProduct(user, req));
    }

    @GetMapping("/search")
    public ResponseEntity<Products> getByEan(
            @RequestParam("ean") String ean,
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.ok(
                productsService.findByEan(user, ean)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Products> editById(
            @AuthenticationPrincipal User user,
            @PathVariable long id,
            @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.ok(productsService.editById(user, id, req));
    }

    @PutMapping("/by-name")
    public ResponseEntity<Products> editByName(
            @AuthenticationPrincipal User user,
            @RequestParam("name") String name,
            @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.ok(productsService.editByName(user, name, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(
            @AuthenticationPrincipal User user,
            @PathVariable long id
    ) {
        productsService.deleteById(user, id);
        return ResponseEntity.ok("Produto deletado com sucesso");
    }

    @DeleteMapping("/by-name")
    public ResponseEntity<String> deleteByName(
            @AuthenticationPrincipal User user,
            @RequestParam("name") String name
    ) {
        productsService.deleteByName(user, name);
        return ResponseEntity.ok("Produto deletado com sucesso");
    }
}
