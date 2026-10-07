package com.deals.user.dto;

import java.time.Instant;

public record SavedCouponResponse(CouponDto coupon, Instant savedAt) {
}
