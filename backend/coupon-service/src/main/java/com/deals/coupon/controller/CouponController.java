package com.deals.coupon.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

import com.deals.coupon.dto.CouponFilter;
import com.deals.coupon.dto.CouponRequest;
import com.deals.coupon.dto.CouponResponse;
import com.deals.coupon.dto.PageResponse;
import com.deals.coupon.service.CouponService;

import jakarta.validation.Valid;

/**
 * HTTP layer: maps URLs to service calls. No business logic here.
 *
 *   GET    /api/coupons                 one page of coupons (filters + paging below)
 *                                       - codes are included ONLY for logged-in users
 *   GET    /api/coupons/categories      all category names, A-Z
 *   GET    /api/coupons/batch?ids=a,b   several coupons by id (used by user-service)
 *   GET    /api/coupons/{id}            get one
 *   POST   /api/coupons                 create
 *   PUT    /api/coupons/{id}            update
 *   DELETE /api/coupons/{id}            delete
 */
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    /**
     * Example: /api/coupons?search=swiggy&category=Food&page=0&size=12&sort=discount,desc
     * Spring builds the Pageable from the page, size and sort parameters automatically.
     * Without them: first page, 12 coupons, the ones expiring soonest first.
     *
     * Everyone can browse, but only logged-in users get the codes.
     * @AuthenticationPrincipal gives us the checked login token, or null for a guest.
     */
    @GetMapping
    public PageResponse<CouponResponse> searchCoupons(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "false") boolean includeExpired,
            @PageableDefault(size = 12, sort = "expiryDate") Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        boolean loggedIn = jwt != null;
        return couponService.searchCoupons(new CouponFilter(search, category, includeExpired), pageable, loggedIn);
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return couponService.getCategories();
    }

    // "ids=a,b,c" is split on the commas into a List automatically
    @GetMapping("/batch")
    public List<CouponResponse> getCouponsByIds(@RequestParam List<String> ids, @AuthenticationPrincipal Jwt jwt) {
        return couponService.getCouponsByIds(ids, jwt != null);
    }

    @GetMapping("/{id}")
    public CouponResponse getCouponById(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return couponService.getCouponById(id, jwt != null);
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
