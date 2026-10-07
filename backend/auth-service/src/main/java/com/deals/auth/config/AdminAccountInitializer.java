package com.deals.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.deals.auth.service.AuthService;

/**
 * Runs once, right after the app starts: makes sure an ADMIN account exists.
 * (Register only ever creates USER accounts, so this is how the first admin appears.)
 */
@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final String adminEmail;
    private final String adminPassword;

    public AdminAccountInitializer(AuthService authService,
                                   @Value("${app.admin.email}") String adminEmail,
                                   @Value("${app.admin.password}") String adminPassword) {
        this.authService = authService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        authService.createAdminIfMissing(adminEmail, adminPassword);
    }
}
