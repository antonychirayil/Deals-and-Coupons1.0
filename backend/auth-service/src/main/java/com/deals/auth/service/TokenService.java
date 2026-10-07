package com.deals.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.deals.auth.entity.UserAccount;

/**
 * Creates JWT (JSON Web Token) login tokens.
 *
 * A JWT has 3 parts separated by dots:  header.payload.signature
 *   header    -> {"alg":"HS256"}
 *   payload   -> the "claims": who the user is and when the token expires
 *   signature -> proves the token was made by us and nobody changed it
 * Paste any token into https://jwt.io to see its contents.
 */
@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long expiryMinutes;

    public TokenService(JwtEncoder jwtEncoder, @Value("${app.jwt.expiry-minutes}") long expiryMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.expiryMinutes = expiryMinutes;
    }

    public String createToken(UserAccount account) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("deals-auth-service")
                .subject(account.getId())                     // "sub": who this token belongs to
                .issuedAt(now)
                .expiresAt(now.plus(expiryMinutes, ChronoUnit.MINUTES))
                .claim("email", account.getEmail())
                .claim("name", account.getName())
                .claim("role", account.getRole().name())     // other services read this to allow/deny
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpirySeconds() {
        return expiryMinutes * 60;
    }
}
