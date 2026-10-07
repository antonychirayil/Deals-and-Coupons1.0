package com.deals.user.exception;

// The user already saved this coupon -> HTTP 409
public class CouponAlreadySavedException extends RuntimeException {

    public CouponAlreadySavedException(String couponId) {
        super("Coupon " + couponId + " is already in your saved list");
    }
}
