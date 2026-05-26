package com.example.security.service;

import com.example.security.dto.request.ProductRequest;
import com.example.security.dto.response.ProductResponse;
import com.example.security.entity.Products;
import com.example.security.entity.User;
import com.example.security.entity.enums.UserRole;
import com.example.security.exception.InsufficientStockException;
import com.example.security.exception.ProductForbiddenException;
import com.example.security.exception.ProductNotFoundException;
import com.example.security.repository.ProductsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductsService {

    private static final Logger log = LoggerFactory.getLogger(ProductsService.class);

    private final ProductsRepository productsRepository;

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public Page<ProductResponse> getAll(Pageable pageable) {
        return productsRepository.findByActive(true, pageable)
                .map(ProductResponse::from);
    }

    public Page<ProductResponse> search(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productsRepository.search(name, category, minPrice, maxPrice, pageable)
                .map(ProductResponse::from);
    }

    public ProductResponse getById(Long id) {
        return productsRepository.findById(id)
                .filter(Products::isActive)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));
    }

    public Products getEntityById(Long id) {
        return productsRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));
    }

    @Transactional
    public ProductResponse createProduct(User user, ProductRequest req) {
        if (user.getRole() != UserRole.SELLER && user.getRole() != UserRole.ADMIN) {
            throw new ProductForbiddenException("Apenas sellers ou admins podem criar produtos");
        }
        Products p = new Products();
        applyRequest(p, req);
        p.setOwner(user);
        Products saved = productsRepository.save(p);
        log.info("Produto criado: id={}, ean={}, owner={}", saved.getId(), saved.getEAN(), user.getEmail());
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse editById(User user, Long id, ProductRequest req) {
        Products p = getEntityById(id);
        assertOwnership(user, p);
        applyRequest(p, req);
        Products saved = productsRepository.save(p);
        log.info("Produto atualizado: id={}, owner={}", id, user.getEmail());
        return ProductResponse.from(saved);
    }

    public ProductResponse findByEan(User user, String ean) {
        Products product = productsRepository.findByEAN(ean)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado com esse EAN"));
        if (product.getOwner() == null || product.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException("Você não tem acesso a este produto");
        }
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse editByName(User user, String name, ProductRequest req) {
        Products p = productsRepository.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado com esse nome"));
        assertOwnership(user, p);
        applyRequest(p, req);
        Products saved = productsRepository.save(p);
        log.info("Produto atualizado por nome: name={}, owner={}", name, user.getEmail());
        return ProductResponse.from(saved);
    }

    @Transactional
    public void deleteById(User user, Long id) {
        Products p = getEntityById(id);
        assertOwnership(user, p);
        p.setActive(false);
        productsRepository.save(p);
        log.info("Produto desativado: id={}, owner={}", id, user.getEmail());
    }

    @Transactional
    public void deleteByName(User user, String name) {
        Products p = productsRepository.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado com esse nome"));
        assertOwnership(user, p);
        p.setActive(false);
        productsRepository.save(p);
        log.info("Produto desativado por nome: name={}, owner={}", name, user.getEmail());
    }

    @Transactional
    public void decrementStock(Long productId, int quantity) {
        Products p = getEntityById(productId);
        if (p.getStockQuantity() < quantity) {
            throw new InsufficientStockException(p.getName(), p.getStockQuantity());
        }
        p.setStockQuantity(p.getStockQuantity() - quantity);
        productsRepository.save(p);
    }

    @Transactional
    public void incrementStock(Long productId, int quantity) {
        Products p = getEntityById(productId);
        p.setStockQuantity(p.getStockQuantity() + quantity);
        productsRepository.save(p);
    }

    private void assertOwnership(User user, Products product) {
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isOwner = product.getOwner() != null && product.getOwner().getId() == user.getId();
        if (!isAdmin && !isOwner) {
            throw new ProductForbiddenException("Apenas o dono do produto ou admin pode realizar esta ação");
        }
    }

    private void applyRequest(Products p, ProductRequest req) {
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setCategory(req.getCategory());
        p.setSubcategory(req.getSubcategory());
        p.setEAN(req.getEAN());
        p.setPrice(req.getPrice());
        p.setImageUrl(req.getImageUrl());
        if (req.getStockQuantity() != null) {
            p.setStockQuantity(req.getStockQuantity());
        }
    }
}
