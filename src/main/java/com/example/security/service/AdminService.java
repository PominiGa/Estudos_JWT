package com.example.security.service;

import com.example.security.dto.request.ChangeUserRoleRequest;
import com.example.security.dto.response.AdminUserResponse;
import com.example.security.dto.response.ProductResponse;
import com.example.security.entity.User;
import com.example.security.entity.enums.UserRole;
import com.example.security.exception.UserNotFoundException;
import com.example.security.repository.ProductsRepository;
import com.example.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final ProductsRepository productsRepository;

    public AdminService(UserRepository userRepository, ProductsRepository productsRepository) {
        this.userRepository = userRepository;
        this.productsRepository = productsRepository;
    }

    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserResponse::from);
    }

    public AdminUserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(AdminUserResponse::from)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public AdminUserResponse changeUserRole(Long id, ChangeUserRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        UserRole oldRole = user.getRole();
        user.setRole(request.getRole());
        User saved = userRepository.save(user);
        log.info("Role do usuario alterada: userId={}, {} -> {}", id, oldRole, request.getRole());
        return AdminUserResponse.from(saved);
    }

    @Transactional
    public void deleteUser(Long id, Long requestingAdminId) {
        if (id.equals(requestingAdminId)) {
            throw new IllegalArgumentException("Admin não pode deletar a si mesmo");
        }
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        log.info("Usuario deletado pelo admin: userId={}", id);
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productsRepository.findAll(pageable).map(ProductResponse::from);
    }
}
