package com.deals.coupon.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deals.coupon.dto.CouponFilter;
import com.deals.coupon.entity.Coupon;

/**
 * Queries that method names like findByCategory... can't express, because every filter is OPTIONAL.
 * Spring Data finds the class named CouponSearchRepositoryImpl and plugs it into CouponRepository.
 */
public interface CouponSearchRepository {

    Page<Coupon> search(CouponFilter filter, Pageable pageable);

    List<String> findAllCategories();
}
