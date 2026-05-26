package com.example.security.service;

import com.example.security.dto.request.UpdateProfileRequest;
import com.example.security.dto.response.UserProfileResponse;
import com.example.security.entity.User;
import com.example.security.exception.EmailAlreadyExistsException;
import com.example.security.exception.UserNotFoundException;
import com.example.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getProfile(User user) {
        User current = userRepository.findById(user.getId())
                .orElseThrow(UserNotFoundException::new);
        return UserProfileResponse.from(current);
    }

    @Transactional
    public UserProfileResponse updateProfile(User user, UpdateProfileRequest request) {
        User current = userRepository.findById(user.getId())
                .orElseThrow(UserNotFoundException::new);

        boolean emailChanged = !current.getEmail().equalsIgnoreCase(request.getEmail());
        if (emailChanged) {
            userRepository.findUserByEmail(request.getEmail())
                    .filter(u -> u.getId() != current.getId())
                    .ifPresent(u -> { throw new EmailAlreadyExistsException(); });
            current.setEmail(request.getEmail());
        }

        current.setName(request.getName());
        User saved = userRepository.save(current);
        log.info("Perfil atualizado: userId={}", saved.getId());
        return UserProfileResponse.from(saved);
    }
}
