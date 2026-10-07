package com.deals.coupon.exception;

// Thrown when no coupon has the requested id -> HTTP 404
public class CouponNotFoundException extends RuntimeException {

    public CouponNotFoundException(String id) {
        super("Coupon not found with id: " + id);
    }
}
