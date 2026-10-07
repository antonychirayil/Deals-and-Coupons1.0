package com.deals.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deals.user.client.CouponClient;
import com.deals.user.dto.CouponDto;
import com.deals.user.dto.SavedCouponResponse;
import com.deals.user.entity.SavedCoupon;
import com.deals.user.exception.CouponAlreadySavedException;
import com.deals.user.exception.CouponNotFoundException;
import com.deals.user.exception.SavedCouponNotFoundException;
import com.deals.user.repository.SavedCouponRepository;

@ExtendWith(MockitoExtension.class)
class SavedCouponServiceTest {

    @Mock
    private SavedCouponRepository savedCouponRepository;

    @Mock // a fake coupon-service: no HTTP calls happen in these tests
    private CouponClient couponClient;

    @InjectMocks
    private SavedCouponService savedCouponService;

    private final CouponDto coupon = new CouponDto(
            "c1", "SAVE20", "Amazon", "Electronics", "20% off", 20.0, LocalDate.of(2099, 12, 31), false);

    @Test
    void saveCoupon_savesWhenCouponExists() {
        when(savedCouponRepository.existsByUserIdAndCouponId("u1", "c1")).thenReturn(false);
        when(couponClient.findCoupon("c1")).thenReturn(Optional.of(coupon));
        when(savedCouponRepository.save(any(SavedCoupon.class))).thenAnswer(call -> call.getArgument(0));

        SavedCouponResponse response = savedCouponService.saveCoupon("u1", "c1");

        assertEquals("SAVE20", response.coupon().code());
    }

    @Test
    void saveCoupon_throwsWhenAlreadySaved() {
        when(savedCouponRepository.existsByUserIdAndCouponId("u1", "c1")).thenReturn(true);

        assertThrows(CouponAlreadySavedException.class, () -> savedCouponService.saveCoupon("u1", "c1"));

        verify(savedCouponRepository, never()).save(any());
    }

    @Test
    void saveCoupon_throwsWhenCouponDoesNotExist() {
        when(savedCouponRepository.existsByUserIdAndCouponId("u1", "missing")).thenReturn(false);
        when(couponClient.findCoupon("missing")).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> savedCouponService.saveCoupon("u1", "missing"));

        verify(savedCouponRepository, never()).save(any());
    }

    @Test
    void getSavedCoupons_skipsCouponsDeletedFromCouponService() {
        when(savedCouponRepository.findByUserIdOrderBySavedAtDesc("u1")).thenReturn(List.of(
                new SavedCoupon("u1", "c1", Instant.now()),
                new SavedCoupon("u1", "deleted-coupon", Instant.now())));
        // coupon-service only knows c1: the deleted one is missing from its answer
        when(couponClient.findCoupons(List.of("c1", "deleted-coupon"))).thenReturn(List.of(coupon));

        List<SavedCouponResponse> result = savedCouponService.getSavedCoupons("u1");

        assertEquals(1, result.size());
        assertEquals("c1", result.get(0).coupon().id());
    }

    @Test
    void removeSavedCoupon_throwsWhenNotSaved() {
        when(savedCouponRepository.deleteByUserIdAndCouponId("u1", "c1")).thenReturn(0L);

        assertThrows(SavedCouponNotFoundException.class, () -> savedCouponService.removeSavedCoupon("u1", "c1"));
    }
}
