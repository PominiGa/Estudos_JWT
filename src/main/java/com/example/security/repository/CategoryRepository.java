package com.example.security.repository;

import com.example.security.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    List<Category> findByActive(boolean active);
    boolean existsByName(String name);
}