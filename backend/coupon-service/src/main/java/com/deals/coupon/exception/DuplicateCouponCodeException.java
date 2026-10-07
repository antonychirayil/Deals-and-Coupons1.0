package com.deals.coupon.exception;

// Thrown when another coupon already uses the same code -> HTTP 409
public class DuplicateCouponCodeException extends RuntimeException {

    public DuplicateCouponCodeException(String code) {
        super("A coupon with code '" + code + "' already exists");
    }
}
