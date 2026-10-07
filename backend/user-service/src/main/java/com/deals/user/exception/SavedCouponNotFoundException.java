package com.deals.user.exception;

// Trying to remove a coupon the user never saved -> HTTP 404
public class SavedCouponNotFoundException extends RuntimeException {

    public SavedCouponNotFoundException(String couponId) {
        super("Coupon " + couponId + " is not in your saved list");
    }
}
