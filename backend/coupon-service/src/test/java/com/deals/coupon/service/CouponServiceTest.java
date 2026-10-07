package com.deals.coupon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deals.coupon.dto.CouponRequest;
import com.deals.coupon.dto.CouponResponse;
import com.deals.coupon.entity.Coupon;
import com.deals.coupon.exception.CouponNotFoundException;
import com.deals.coupon.exception.DuplicateCouponCodeException;
import com.deals.coupon.repository.CouponRepository;

/**
 * Unit tests: CouponService alone, with a FAKE repository (a mock).
 * No Spring, no MongoDB, so these run in milliseconds.
 */
@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock // a fake CouponRepository; we decide what each method returns
    private CouponRepository couponRepository;

    // A clock frozen at 00:30 on 8 Oct 2026 in India (= 19:00 on 7 Oct in UTC).
    // A fixed clock makes "today" the same every time the tests run.
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-07T19:00:00Z"), ZoneId.of("Asia/Kolkata"));

    private CouponService couponService;

    @BeforeEach // a real CouponService, built with the fake repository and the fixed clock
    void setUp() {
        couponService = new CouponService(couponRepository, clock);
    }

    private final CouponRequest request = new CouponRequest(
            " save20 ", "Amazon", "Electronics", "20% off", 20.0, LocalDate.of(2099, 12, 31));

    @Test
    void createCoupon_savesCodeInUppercase() {
        // Arrange: no coupon has this code yet, and save() returns whatever it is given
        when(couponRepository.existsByCode("SAVE20")).thenReturn(false);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(call -> call.getArgument(0));

        // Act
        CouponResponse response = couponService.createCoupon(request);

        // Assert
        assertEquals("SAVE20", response.code());
        assertEquals("Amazon", response.provider());
    }

    @Test
    void createCoupon_throwsWhenCodeAlreadyExists() {
        when(couponRepository.existsByCode("SAVE20")).thenReturn(true);

        assertThrows(DuplicateCouponCodeException.class, () -> couponService.createCoupon(request));

        // Make sure nothing was written to the database
        verify(couponRepository, never()).save(any());
    }

    @Test
    void getCouponById_throwsWhenNotFound() {
        when(couponRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> couponService.getCouponById("missing-id"));
    }

    @Test
    void deleteCoupon_deletesWhenFound() {
        when(couponRepository.existsById("abc")).thenReturn(true);

        couponService.deleteCoupon("abc");

        verify(couponRepository).deleteById("abc");
    }

    @Test
    void deleteCoupon_throwsWhenNotFound() {
        when(couponRepository.existsById("missing-id")).thenReturn(false);

        assertThrows(CouponNotFoundException.class, () -> couponService.deleteCoupon("missing-id"));

        verify(couponRepository, never()).deleteById(any());
    }

    @Test
    void expiredFlag_usesIndiaDateEvenWhenUtcIsStillYesterday() {
        // It's 00:30 on 8 Oct in India, but still 7 Oct in UTC.
        Coupon endedYesterday = new Coupon("OLD", "Amazon", "Electronics", "", 10.0, LocalDate.of(2026, 10, 7));
        Coupon endsToday = new Coupon("NOW", "Amazon", "Electronics", "", 10.0, LocalDate.of(2026, 10, 8));
        when(couponRepository.findById("old")).thenReturn(Optional.of(endedYesterday));
        when(couponRepository.findById("now")).thenReturn(Optional.of(endsToday));

        assertTrue(couponService.getCouponById("old").expired());  // a UTC clock would still say "valid"
        assertFalse(couponService.getCouponById("now").expired()); // valid until the end of today
    }
}
