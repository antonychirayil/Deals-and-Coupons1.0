package com.deals.coupon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.deals.coupon.entity.Coupon;

/**
 * Database access for coupons. We only write the interface:
 * Spring Data creates the implementation at startup.
 *
 * Inherited for free: save, findById, findAllById, deleteById, existsById, count ...
 * From CouponSearchRepository: search(filter, pageable) and findAllCategories().
 */
public interface CouponRepository extends MongoRepository<Coupon, String>, CouponSearchRepository {

    // -> { code: <code> } and returns true/false
    boolean existsByCode(String code);
}
