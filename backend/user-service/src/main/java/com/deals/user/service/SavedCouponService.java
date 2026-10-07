package com.deals.user.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.deals.user.client.CouponClient;
import com.deals.user.dto.CouponDto;
import com.deals.user.dto.SavedCouponResponse;
import com.deals.user.entity.SavedCoupon;
import com.deals.user.exception.CouponAlreadySavedException;
import com.deals.user.exception.CouponNotFoundException;
import com.deals.user.exception.SavedCouponNotFoundException;
import com.deals.user.repository.SavedCouponRepository;

@Service
public class SavedCouponService {

    private static final Logger log = LoggerFactory.getLogger(SavedCouponService.class);

    private final SavedCouponRepository savedCouponRepository;
    private final CouponClient couponClient;

    public SavedCouponService(SavedCouponRepository savedCouponRepository, CouponClient couponClient) {
        this.savedCouponRepository = savedCouponRepository;
        this.couponClient = couponClient;
    }

    public List<SavedCouponResponse> getSavedCoupons(String userId) {
        List<SavedCouponResponse> result = new ArrayList<>();

        for (SavedCoupon saved : savedCouponRepository.findByUserIdOrderBySavedAtDesc(userId)) {
            // One HTTP call per saved coupon. Fine for a few coupons; Phase 8 could add a "get many" endpoint.
            Optional<CouponDto> coupon = couponClient.findCoupon(saved.getCouponId());

            // An admin may have deleted the coupon since it was saved: just skip it
            coupon.ifPresent(c -> result.add(new SavedCouponResponse(c, saved.getSavedAt())));
        }
        return result;
    }

    public SavedCouponResponse saveCoupon(String userId, String couponId) {
        if (savedCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new CouponAlreadySavedException(couponId);
        }

        // Ask coupon-service first: never save an id that doesn't exist
        CouponDto coupon = couponClient.findCoupon(couponId)
                .orElseThrow(() -> new CouponNotFoundException(couponId));

        SavedCoupon saved = savedCouponRepository.save(new SavedCoupon(userId, couponId, Instant.now()));
        log.info("User {} saved coupon {}", userId, couponId);
        return new SavedCouponResponse(coupon, saved.getSavedAt());
    }

    public void removeSavedCoupon(String userId, String couponId) {
        long deleted = savedCouponRepository.deleteByUserIdAndCouponId(userId, couponId);
        if (deleted == 0) {
            throw new SavedCouponNotFoundException(couponId);
        }
        log.info("User {} removed saved coupon {}", userId, couponId);
    }
}
