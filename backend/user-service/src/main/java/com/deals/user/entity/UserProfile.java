package com.deals.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Extra profile details. Name and email live in auth-service (one owner per piece of data),
 * so this document only holds what auth-service doesn't know.
 */
@Document(collection = "user_profiles")
public class UserProfile {

    @Id
    private String userId; // the same id as in the token's "sub" claim, so one profile per user

    private String phone;
    private String city;

    public UserProfile() {
    }

    public UserProfile(String userId, String phone, String city) {
        this.userId = userId;
        this.phone = phone;
        this.city = city;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }
}
