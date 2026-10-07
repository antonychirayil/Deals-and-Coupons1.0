package com.deals.coupon.dto;

/**
 * The optional filters for searching coupons. A null/blank value means "don't filter on this".
 *
 * @param search         text to look for in provider, description or category (any case)
 * @param category       exact category, e.g. "Food"
 * @param includeExpired false = only coupons that are still valid
 */
public record CouponFilter(String search, String category, boolean includeExpired) {
}
