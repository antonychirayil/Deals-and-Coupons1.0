package com.deals.user.dto;

import java.time.LocalDate;

/**
 * A coupon as coupon-service sends it. This is a COPY of coupon-service's CouponResponse:
 * microservices don't share Java classes, so each one can change and deploy on its own.
 * Only the fields we need are listed; Jackson ignores the rest.
 */
public record CouponDto(
        String id,
        String code,
        String provider,
        String category,
        String description,
        Double discount,
        LocalDate expiryDate,
        boolean expired) {
}
