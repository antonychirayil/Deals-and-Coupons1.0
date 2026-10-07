package com.deals.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.deals.auth.dto.LoginRequest;
import com.deals.auth.dto.LoginResponse;
import com.deals.auth.dto.RegisterRequest;
import com.deals.auth.entity.Role;
import com.deals.auth.entity.UserAccount;
import com.deals.auth.exception.EmailAlreadyUsedException;
import com.deals.auth.exception.InvalidCredentialsException;
import com.deals.auth.repository.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private TokenService tokenService;

    // A REAL BCrypt encoder: we want to test that hashing really happens
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthService authService;

    @BeforeEach // runs before every test, so each test gets a fresh AuthService
    void setUp() {
        authService = new AuthService(userAccountRepository, passwordEncoder, tokenService);
    }

    @Test
    void register_storesHashedPasswordNotPlainText() {
        when(userAccountRepository.existsByEmail("asha@mail.com")).thenReturn(false);
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(call -> call.getArgument(0));

        authService.register(new RegisterRequest("Asha", " Asha@Mail.com ", "password123"));

        // ArgumentCaptor grabs the object that was passed to save(), so we can inspect it
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).save(captor.capture());
        UserAccount saved = captor.getValue();

        assertEquals("asha@mail.com", saved.getEmail());
        assertEquals(Role.USER, saved.getRole());
        assertNotEquals("password123", saved.getPasswordHash());
        assertTrue(passwordEncoder.matches("password123", saved.getPasswordHash()));
    }

    @Test
    void register_throwsWhenEmailAlreadyUsed() {
        when(userAccountRepository.existsByEmail("asha@mail.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class,
                () -> authService.register(new RegisterRequest("Asha", "asha@mail.com", "password123")));

        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void login_returnsTokenWhenPasswordCorrect() {
        UserAccount account = new UserAccount("Asha", "asha@mail.com", passwordEncoder.encode("password123"), Role.USER);
        when(userAccountRepository.findByEmail("asha@mail.com")).thenReturn(Optional.of(account));
        when(tokenService.createToken(account)).thenReturn("fake-token");

        LoginResponse response = authService.login(new LoginRequest("asha@mail.com", "password123"));

        assertEquals("fake-token", response.token());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void login_throwsWhenPasswordWrong() {
        UserAccount account = new UserAccount("Asha", "asha@mail.com", passwordEncoder.encode("password123"), Role.USER);
        when(userAccountRepository.findByEmail("asha@mail.com")).thenReturn(Optional.of(account));

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("asha@mail.com", "wrong-password")));

        verify(tokenService, never()).createToken(any());
    }

    @Test
    void login_throwsWhenEmailUnknown() {
        when(userAccountRepository.findByEmail("nobody@mail.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("nobody@mail.com", "password123")));
    }
}
