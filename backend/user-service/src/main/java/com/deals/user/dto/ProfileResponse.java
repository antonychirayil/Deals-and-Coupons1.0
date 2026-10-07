package com.deals.user.dto;

// Combines token data (name, email) with stored profile data (phone, city)
public record ProfileResponse(String userId, String name, String email, String phone, String city) {
}
