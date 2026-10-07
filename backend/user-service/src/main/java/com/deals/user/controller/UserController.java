package com.deals.user.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deals.user.dto.CurrentUser;
import com.deals.user.dto.ProfileRequest;
import com.deals.user.dto.ProfileResponse;
import com.deals.user.dto.SavedCouponResponse;
import com.deals.user.service.ProfileService;
import com.deals.user.service.SavedCouponService;

import jakarta.validation.Valid;

/**
 * Every URL is under /me: a user can only ever see and change THEIR OWN data.
 * The user id comes from the verified token, never from the URL or request body.
 *
 *   GET    /api/users/me/profile
 *   PUT    /api/users/me/profile
 *   GET    /api/users/me/saved-coupons
 *   POST   /api/users/me/saved-coupons/{couponId}
 *   DELETE /api/users/me/saved-coupons/{couponId}
 */
@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final ProfileService profileService;
    private final SavedCouponService savedCouponService;

    public UserController(ProfileService profileService, SavedCouponService savedCouponService) {
        this.profileService = profileService;
        this.savedCouponService = savedCouponService;
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@AuthenticationPrincipal Jwt jwt) {
        return profileService.getProfile(CurrentUser.from(jwt));
    }

    @PutMapping("/profile")
    public ProfileResponse updateProfile(@AuthenticationPrincipal Jwt jwt,
                                         @Valid @RequestBody ProfileRequest request) {
        return profileService.updateProfile(CurrentUser.from(jwt), request);
    }

    @GetMapping("/saved-coupons")
    public List<SavedCouponResponse> getSavedCoupons(@AuthenticationPrincipal Jwt jwt) {
        return savedCouponService.getSavedCoupons(jwt.getSubject());
    }

    @PostMapping("/saved-coupons/{couponId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavedCouponResponse saveCoupon(@AuthenticationPrincipal Jwt jwt, @PathVariable String couponId) {
        return savedCouponService.saveCoupon(jwt.getSubject(), couponId);
    }

    @DeleteMapping("/saved-coupons/{couponId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSavedCoupon(@AuthenticationPrincipal Jwt jwt, @PathVariable String couponId) {
        savedCouponService.removeSavedCoupon(jwt.getSubject(), couponId);
    }
}
