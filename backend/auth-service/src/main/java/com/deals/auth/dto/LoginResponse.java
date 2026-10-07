package com.deals.auth.dto;

// Sent back after a successful login. The client stores the token
// and sends it with every request:  Authorization: Bearer <token>
public record LoginResponse(String token, String tokenType, long expiresInSeconds, UserResponse user) {
}
