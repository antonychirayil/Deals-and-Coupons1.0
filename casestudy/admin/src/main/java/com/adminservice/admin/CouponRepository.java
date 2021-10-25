package com.adminservice.admin;

import com.couponservice.coupon.Coupon;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CouponRepository extends MongoRepository<Coupon, String> {

    Coupon findProductById(String Id);

}
