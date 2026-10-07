package com.deals.coupon.dto;

import java.time.LocalDate;

import com.deals.coupon.entity.Coupon;

/**
 * What the API sends back to clients (JSON response body).
 */
public record CouponResponse(
        String id,
        String code,
        String provider,
        String category,
        String description,
        Double discount,
        LocalDate expiryDate,
        boolean expired) {

    // Converts the database entity into the API shape.
    // "today" is passed in (from the service's Clock) instead of calling LocalDate.now() here.
    // includeCode = false for guests: they see the coupon, but "code" is null in the JSON.
    public static CouponResponse from(Coupon coupon, LocalDate today, boolean includeCode) {
        boolean expired = coupon.getExpiryDate().isBefore(today);
        return new CouponResponse(
                coupon.getId(),
                includeCode ? coupon.getCode() : null,
                coupon.getProvider(),
                coupon.getCategory(),
                coupon.getDescription(),
                coupon.getDiscount(),
                coupon.getExpiryDate(),
                expired);
    }
}
