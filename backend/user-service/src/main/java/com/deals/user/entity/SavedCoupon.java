package com.deals.user.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * "User X saved coupon Y at time Z". We store only the coupon's ID, not a copy of the coupon:
 * coupon-service owns coupon data, so we always ask it for the latest details.
 */
@Document(collection = "saved_coupons")
@CompoundIndex(name = "user_coupon_unique", def = "{'userId': 1, 'couponId': 1}", unique = true)
public class SavedCoupon {

    @Id
    private String id;

    private String userId;
    private String couponId;
    private Instant savedAt; // Instant = an exact moment in time (UTC)

    public SavedCoupon() {
    }

    public SavedCoupon(String userId, String couponId, Instant savedAt) {
        this.userId = userId;
        this.couponId = couponId;
        this.savedAt = savedAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCouponId() {
        return couponId;
    }

    public Instant getSavedAt() {
        return savedAt;
    }
}
