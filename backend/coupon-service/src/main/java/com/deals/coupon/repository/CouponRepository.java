package com.deals.coupon.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.deals.coupon.entity.Coupon;

/**
 * Database access for coupons. We only write the interface:
 * Spring Data creates the implementation at startup.
 *
 * Inherited for free: save, findById, findAll, deleteById, existsById, count ...
 * Extra queries are generated from the method NAME.
 */
public interface CouponRepository extends MongoRepository<Coupon, String> {

    // -> { category: <category> }, ignoring upper/lower case
    List<Coupon> findByCategoryIgnoreCase(String category);

    // -> { code: <code> } and returns true/false
    boolean existsByCode(String code);
}
