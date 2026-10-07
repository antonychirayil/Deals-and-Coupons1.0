package com.deals.user.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.deals.user.entity.SavedCoupon;

public interface SavedCouponRepository extends MongoRepository<SavedCoupon, String> {

    // Newest first
    List<SavedCoupon> findByUserIdOrderBySavedAtDesc(String userId);

    boolean existsByUserIdAndCouponId(String userId, String couponId);

    // Returns how many documents were deleted (0 or 1)
    long deleteByUserIdAndCouponId(String userId, String couponId);
}
