package com.couponservice.coupon;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CouponRepository extends MongoRepository<Coupon, String> {

    Coupon findProductById(String Id);

}
