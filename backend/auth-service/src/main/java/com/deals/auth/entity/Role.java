package com.deals.auth.entity;

// What a user is allowed to do. An enum allows only these fixed values.
public enum Role {
    USER,  // can browse and save coupons
    ADMIN  // can also create, edit and delete coupons
}
