package com.deals.coupon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deals.coupon.dto.CouponRequest;
import com.deals.coupon.dto.CouponResponse;
import com.deals.coupon.service.CouponService;

import jakarta.validation.Valid;

/**
 * HTTP layer: maps URLs to service calls. No business logic here.
 *
 *   GET    /api/coupons              list all (optional ?category=Electronics)
 *   GET    /api/coupons/{id}         get one
 *   POST   /api/coupons              create
 *   PUT    /api/coupons/{id}         update
 *   DELETE /api/coupons/{id}         delete
 */
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public List<CouponResponse> getAllCoupons(@RequestParam(required = false) String category) {
        return couponService.getAllCoupons(category);
    }

    @GetMapping("/{id}")
    public CouponResponse getCouponById(@PathVariable String id) {
        return couponService.getCouponById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 instead of the default 200
    public CouponResponse createCoupon(@Valid @RequestBody CouponRequest request) {
        return couponService.createCoupon(request);
    }

    @PutMapping("/{id}")
    public CouponResponse updateCoupon(@PathVariable String id,
                                       @Valid @RequestBody CouponRequest request) {
        return couponService.updateCoupon(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: success, nothing to send back
    public void deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
    }
}
