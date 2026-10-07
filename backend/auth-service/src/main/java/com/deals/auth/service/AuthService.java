package com.deals.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.deals.auth.dto.LoginRequest;
import com.deals.auth.dto.LoginResponse;
import com.deals.auth.dto.RegisterRequest;
import com.deals.auth.dto.UserResponse;
import com.deals.auth.entity.Role;
import com.deals.auth.entity.UserAccount;
import com.deals.auth.exception.EmailAlreadyUsedException;
import com.deals.auth.exception.InvalidCredentialsException;
import com.deals.auth.repository.UserAccountRepository;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userAccountRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }

        String passwordHash = passwordEncoder.encode(request.password());
        UserAccount saved = userAccountRepository.save(
                new UserAccount(request.name(), email, passwordHash, Role.USER)); // new sign-ups are never admins

        log.info("Registered new user {}", email);
        return UserResponse.from(saved);
    }

    public LoginResponse login(LoginRequest request) {
        UserAccount account = userAccountRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(InvalidCredentialsException::new);

        // matches() hashes the typed password the same way and compares. The hash can't be "decrypted".
        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = tokenService.createToken(account);
        return new LoginResponse(token, "Bearer", tokenService.getExpirySeconds(), UserResponse.from(account));
    }

    public UserResponse getUserById(String userId) {
        return userAccountRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(InvalidCredentialsException::new); // token is valid but the account was deleted
    }

    public void createAdminIfMissing(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (userAccountRepository.existsByEmail(normalizedEmail)) {
            return;
        }
        userAccountRepository.save(
                new UserAccount("Admin", normalizedEmail, passwordEncoder.encode(password), Role.ADMIN));
        log.info("Created admin account {}", normalizedEmail);
    }

    // "  John@Mail.com " and "john@mail.com" are the same account
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
