package com.example.security.service;

import com.example.security.dto.request.ProductRequest;
import com.example.security.entity.Products;
import com.example.security.entity.User;
import com.example.security.entity.enums.UserRole;
import com.example.security.exception.ProductForbiddenException;
import com.example.security.exception.ProductNotFoundException;
import com.example.security.repository.ProductsRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public List<Products> getAll() {
        return productsRepository.findAll();
    }

    public Products getById(long id) {
        return productsRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));
    }

    public List<Products> getByName(String name) {
        return productsRepository.findByNameContainingIgnoreCase(name);
    }

    public Products createProduct(User user, ProductRequest req) {
        if (user.getRole() != UserRole.SELLER && user.getRole() != UserRole.ADMIN) {
            throw new ProductForbiddenException("Apenas sellers ou admins podem criar produtos");
        }

        Products p = new Products();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setCategory(req.getCategory());
        p.setSubcategory(req.getSubcategory());
        p.setEAN(req.getEAN());
        p.setPrice(req.getPrice());
        p.setOwner(user);

        return productsRepository.save(p);
    }

    public Products editById(User user, long id, ProductRequest req) {
        Products p = getById(id);
        if (p.getOwner() == null || p.getOwner().getId() != user.getId()) {
            throw new ProductForbiddenException("Apenas o dono do produto pode editá-lo");
        }

        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setCategory(req.getCategory());
        p.setSubcategory(req.getSubcategory());
        p.setEAN(req.getEAN());
        p.setPrice(req.getPrice());

        return productsRepository.save(p);
    }

    public Products findByEan(User user, String ean) {

        Products product = productsRepository.findByEan(ean)
                .orElseThrow(ProductNotFoundException::new);

        boolean isOwner =
                product.getOwner().getId() == user.getId();

        if (!isOwner) {
            throw new AccessDeniedException(
                    "Você não pode acessar este produto"
            );
        }

        return product;
    }

    public Products editByName(User user, String name, ProductRequest req) {
        Products p = productsRepository.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado com esse nome"));

        if (p.getOwner() == null || p.getOwner().getId() != user.getId()) {
            throw new ProductForbiddenException("Apenas o dono do produto pode editá-lo");
        }

        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setCategory(req.getCategory());
        p.setSubcategory(req.getSubcategory());
        p.setEAN(req.getEAN());
        p.setPrice(req.getPrice());

        return productsRepository.save(p);
    }

    public void deleteById(User user, long id) {
        Products p = getById(id);
        if (p.getOwner() == null || p.getOwner().getId() != user.getId()) {
            throw new ProductForbiddenException("Apenas o dono do produto pode deletá-lo");
        }
        productsRepository.delete(p);
    }

    public void deleteByName(User user, String name) {
        Products p = productsRepository.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado com esse nome"));
        if (p.getOwner() == null || p.getOwner().getId() != user.getId()) {
            throw new ProductForbiddenException("Apenas o dono do produto pode deletá-lo");
        }
        productsRepository.delete(p);
    }
}
