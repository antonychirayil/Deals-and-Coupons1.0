package com.deals.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deals.auth.dto.LoginRequest;
import com.deals.auth.dto.LoginResponse;
import com.deals.auth.dto.RegisterRequest;
import com.deals.auth.dto.UserResponse;
import com.deals.auth.service.AuthService;

import jakarta.validation.Valid;

/**
 *   POST /api/auth/register   public    create a USER account
 *   POST /api/auth/login      public    get a token
 *   GET  /api/auth/me         needs token: who am I?
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Spring Security has already checked the token before this runs;
    // @AuthenticationPrincipal hands us the decoded token.
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return authService.getUserById(jwt.getSubject());
    }
}
