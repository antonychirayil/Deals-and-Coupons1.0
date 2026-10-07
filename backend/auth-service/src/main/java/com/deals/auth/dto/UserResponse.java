package com.deals.auth.dto;

import com.deals.auth.entity.UserAccount;

// Public view of an account: note there is no password field at all
public record UserResponse(String id, String name, String email, String role) {

    public static UserResponse from(UserAccount account) {
        return new UserResponse(account.getId(), account.getName(), account.getEmail(), account.getRole().name());
    }
}
