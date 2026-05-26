package com.example.security.controller;

import com.example.security.dto.ApiResponse;
import com.example.security.dto.request.UpdateOrderStatusRequest;
import com.example.security.dto.response.OrderResponse;
import com.example.security.entity.User;
import com.example.security.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createFromCart(@AuthenticationPrincipal User user) {
        return ApiResponse.created("Pedido criado com sucesso", orderService.createFromCart(user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(@AuthenticationPrincipal User user) {
        return ApiResponse.ok("Histórico de pedidos", orderService.getMyOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrderById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ApiResponse.ok("Pedido", orderService.getMyOrderById(user, id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ApiResponse.ok("Pedido cancelado", orderService.cancelOrder(user, id));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok("Todos os pedidos", orderService.getAllOrders(pageable));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ApiResponse.ok("Status atualizado", orderService.updateStatus(id, request.getStatus()));
    }
}
