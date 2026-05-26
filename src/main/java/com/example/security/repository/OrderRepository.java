package com.example.security.repository;

import com.example.security.entity.Order;
import com.example.security.entity.User;
import com.example.security.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByIdAndUser(Long id, User user);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
