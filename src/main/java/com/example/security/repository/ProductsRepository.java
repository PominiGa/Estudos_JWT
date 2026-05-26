package com.example.security.repository;

import com.example.security.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<Products, Long> {

    List<Products> findByNameContainingIgnoreCase(String name);

    Page<Products> findByActive(boolean active, Pageable pageable);

    Page<Products> findByActiveAndNameContainingIgnoreCase(boolean active, String name, Pageable pageable);

    Page<Products> findByActiveAndCategoryIgnoreCase(boolean active, String category, Pageable pageable);

    @Query("SELECT p FROM Products p WHERE p.active = true " +
           "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:category IS NULL OR LOWER(p.category) = LOWER(:category)) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Products> search(
            @Param("name") String name,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    Optional<Products> findByName(String name);

    Optional<Products> findByEAN(String ean);

    void deleteByName(String name);
}
