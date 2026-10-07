package com.deals.auth.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Login details for one person, stored in the "user_accounts" collection.
 * We NEVER store the real password, only its BCrypt hash.
 */
@Document(collection = "user_accounts")
public class UserAccount {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true) // one account per email
    private String email;

    private String passwordHash; // e.g. "$2a$10$N9qo8uLOickgx2ZMRZoMye..."

    private Role role;

    public UserAccount() {
    }

    public UserAccount(String name, String email, String passwordHash, Role role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }
}
