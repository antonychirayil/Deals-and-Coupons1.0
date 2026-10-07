package com.deals.user.dto;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * The logged-in user, read from the token's claims (no database or auth-service call needed).
 * Converting the Jwt here keeps Spring Security types out of the service layer.
 */
public record CurrentUser(String id, String name, String email) {

    public static CurrentUser from(Jwt jwt) {
        return new CurrentUser(jwt.getSubject(), jwt.getClaimAsString("name"), jwt.getClaimAsString("email"));
    }
}
