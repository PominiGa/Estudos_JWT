package com.example.security.service;

import com.example.security.config.TokenConfig;
import com.example.security.dto.ChangePasswordDTO;
import com.example.security.dto.DeleteUserDTO;
import com.example.security.dto.request.LoginRequest;
import com.example.security.dto.request.RegisterUserRequest;
import com.example.security.dto.response.LoginResponse;
import com.example.security.dto.response.RegisterUserResponse;
import com.example.security.entity.User;
import com.example.security.entity.enums.UserRole;
import com.example.security.exception.*;
import com.example.security.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       TokenConfig tokenConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken userAndPass =
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                );

        Authentication authentication =
                authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();

        String token = tokenConfig.generateToken(user);

        return new LoginResponse(token);
    }

    public RegisterUserResponse register(RegisterUserRequest request) {

        boolean userExists = userRepository.findUserByEmail(request.email()).isPresent();

        if (userExists) {
            throw new EmailAlreadyExistsException();
        }

        User newUser = new User();

        newUser.setName(request.name());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(UserRole.CURRENT);

        userRepository.save(newUser);

        return new RegisterUserResponse(
                newUser.getName(),
                newUser.getEmail()
        );
    }

    public void changePassword(String email, @Valid ChangePasswordDTO dto) {

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        boolean passwordCorrect = passwordEncoder.matches(
                dto.getOldPassword(),
                user.getPassword()
        );

        if (!passwordCorrect) {
            throw new PasswordInvalidException();
        }

        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new SamePasswordException();
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordConfirmationException();
        }

        user.setPassword(
                passwordEncoder.encode(dto.getNewPassword())
        );

        userRepository.save(user);
    }

    public void deleteUser(String email, DeleteUserDTO dto) {

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        boolean passwordCorrect = passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword()
        );

        if (!passwordCorrect) {
            throw new PasswordInvalidException();
        }

        userRepository.delete(user);
    }
}