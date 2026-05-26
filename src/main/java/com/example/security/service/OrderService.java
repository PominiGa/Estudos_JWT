package com.example.security.service;

import com.example.security.dto.response.OrderResponse;
import com.example.security.entity.*;
import com.example.security.entity.enums.OrderStatus;
import com.example.security.exception.*;
import com.example.security.repository.CartRepository;
import com.example.security.repository.OrderRepository;
import com.example.security.repository.ProductsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductsRepository productsRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductsRepository productsRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productsRepository = productsRepository;
    }

    @Transactional
    public OrderResponse createFromCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException());

        if (cart.getItems().isEmpty()) {
            throw new OrderStatusException("O carrinho está vazio");
        }

        for (CartItem cartItem : cart.getItems()) {
            Products product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(product.getName(), product.getStockQuantity());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Products product = cartItem.getProduct();
            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity());
            order.getItems().add(orderItem);
            total = total.add(orderItem.getSubtotal());

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productsRepository.save(product);
        }

        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Pedido criado: id={}, user={}, total={}", saved.getId(), user.getEmail(), total);
        return OrderResponse.from(saved);
    }

    public List<OrderResponse> getMyOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse getMyOrderById(User user, Long id) {
        return orderRepository.findByIdAndUser(id, user)
                .map(OrderResponse::from)
                .orElseThrow(OrderNotFoundException::new);
    }

    @Transactional
    public OrderResponse cancelOrder(User user, Long id) {
        Order order = orderRepository.findByIdAndUser(id, user)
                .orElseThrow(OrderNotFoundException::new);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderStatusException("Apenas pedidos PENDING podem ser cancelados pelo usuário");
        }

        order.setStatus(OrderStatus.CANCELLED);
        restoreStock(order);
        Order saved = orderRepository.save(order);
        log.info("Pedido cancelado: id={}, user={}", id, user.getEmail());
        return OrderResponse.from(saved);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);

        validateStatusTransition(order.getStatus(), newStatus);

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        log.info("Status do pedido atualizado: id={}, status={}", id, newStatus);
        return OrderResponse.from(saved);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.DELIVERED || current == OrderStatus.CANCELLED) {
            throw new OrderStatusException("Pedido já finalizado, não pode ser alterado");
        }
        if (next == OrderStatus.PENDING) {
            throw new OrderStatusException("Não é possível reverter para PENDING");
        }
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Products product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productsRepository.save(product);
        }
    }
}
