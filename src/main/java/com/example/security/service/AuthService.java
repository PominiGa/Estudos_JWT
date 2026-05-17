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

    public void changeUserForSeller(String email, String document) {

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (user.getRole() != UserRole.CURRENT) {
            throw new com.example.security.exception.DocumentInvalidException("Usuário não pode ser promovido para seller");
        }

        String onlyDigits = document.replaceAll("\\D+", "");
        boolean valid;

        if (onlyDigits.length() == 11) {
            valid = isValidCPF(onlyDigits);
        } else if (onlyDigits.length() == 14) {
            valid = isValidCNPJ(onlyDigits);
        } else {
            throw new com.example.security.exception.DocumentInvalidException("Documento deve ser um CPF ou CNPJ válido");
        }

        if (!valid) {
            throw new com.example.security.exception.DocumentInvalidException("Documento inválido");
        }

        user.setRole(UserRole.SELLER);
        userRepository.save(user);
    }

    private boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return false;
        if (cpf.matches("^(\\d)\\1{10}$")) return false;

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstCheck = 11 - (sum % 11);
            if (firstCheck >= 10) firstCheck = 0;
            if (firstCheck != Character.getNumericValue(cpf.charAt(9))) return false;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondCheck = 11 - (sum % 11);
            if (secondCheck >= 10) secondCheck = 0;
            return secondCheck == Character.getNumericValue(cpf.charAt(10));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isValidCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return false;
        if (cnpj.matches("^(\\d)\\1{13}$")) return false;

        try {
            int[] weight1 = {5,4,3,2,9,8,7,6,5,4,3,2};
            int[] weight2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};

            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum += Character.getNumericValue(cnpj.charAt(i)) * weight1[i];
            }
            int firstCheck = sum % 11;
            firstCheck = firstCheck < 2 ? 0 : 11 - firstCheck;
            if (firstCheck != Character.getNumericValue(cnpj.charAt(12))) return false;

            sum = 0;
            for (int i = 0; i < 13; i++) {
                sum += Character.getNumericValue(cnpj.charAt(i)) * weight2[i];
            }
            int secondCheck = sum % 11;
            secondCheck = secondCheck < 2 ? 0 : 11 - secondCheck;
            return secondCheck == Character.getNumericValue(cnpj.charAt(13));
        } catch (NumberFormatException ex) {
            return false;
        }
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