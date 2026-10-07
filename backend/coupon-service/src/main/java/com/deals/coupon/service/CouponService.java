package com.deals.coupon.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.deals.coupon.dto.CouponFilter;
import com.deals.coupon.dto.CouponRequest;
import com.deals.coupon.dto.CouponResponse;
import com.deals.coupon.dto.PageResponse;
import com.deals.coupon.entity.Coupon;
import com.deals.coupon.exception.CouponNotFoundException;
import com.deals.coupon.exception.DuplicateCouponCodeException;
import com.deals.coupon.repository.CouponRepository;

/**
 * Business logic for coupons. The controller calls this class;
 * this class calls the repository. It never deals with HTTP.
 */
@Service
public class CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;

    // Constructor injection: Spring passes in the repository when it creates this service
    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public PageResponse<CouponResponse> searchCoupons(CouponFilter filter, Pageable pageable) {
        // Page.map() converts every Coupon on the page into a CouponResponse, keeping the page info
        return PageResponse.from(couponRepository.search(filter, pageable).map(CouponResponse::from));
    }

    public List<String> getCategories() {
        return couponRepository.findAllCategories();
    }

    // Used by user-service: fetch many coupons in ONE request instead of one request per coupon
    public List<CouponResponse> getCouponsByIds(List<String> ids) {
        return couponRepository.findAllById(ids).stream()
                .map(CouponResponse::from)
                .toList();
    }

    public CouponResponse getCouponById(String id) {
        return CouponResponse.from(findCouponOrThrow(id));
    }

    public CouponResponse createCoupon(CouponRequest request) {
        String code = normalizeCode(request.code());
        if (couponRepository.existsByCode(code)) {
            throw new DuplicateCouponCodeException(code);
        }

        Coupon coupon = new Coupon(code, request.provider(), request.category(),
                request.description(), request.discount(), request.expiryDate());
        Coupon saved = couponRepository.save(coupon);

        log.info("Created coupon {} with id {}", saved.getCode(), saved.getId());
        return CouponResponse.from(saved);
    }

    public CouponResponse updateCoupon(String id, CouponRequest request) {
        Coupon coupon = findCouponOrThrow(id);

        String newCode = normalizeCode(request.code());
        boolean codeChanged = !newCode.equals(coupon.getCode());
        if (codeChanged && couponRepository.existsByCode(newCode)) {
            throw new DuplicateCouponCodeException(newCode);
        }

        coupon.setCode(newCode);
        coupon.setProvider(request.provider());
        coupon.setCategory(request.category());
        coupon.setDescription(request.description());
        coupon.setDiscount(request.discount());
        coupon.setExpiryDate(request.expiryDate());

        log.info("Updated coupon {}", id);
        return CouponResponse.from(couponRepository.save(coupon));
    }

    public void deleteCoupon(String id) {
        if (!couponRepository.existsById(id)) {
            throw new CouponNotFoundException(id);
        }
        couponRepository.deleteById(id);
        log.info("Deleted coupon {}", id);
    }

    private Coupon findCouponOrThrow(String id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
    }

    // Business rule: codes are stored trimmed and in capitals, so "save20 " and "SAVE20" are the same code
    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }
}
