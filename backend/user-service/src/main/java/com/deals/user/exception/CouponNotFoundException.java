package com.deals.user.exception;

// coupon-service has no coupon with this id -> HTTP 404
public class CouponNotFoundException extends RuntimeException {

    public CouponNotFoundException(String couponId) {
        super("Coupon not found with id: " + couponId);
    }
}
