package com.deals.user.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.deals.user.dto.CouponDto;

/**
 * Talks to coupon-service over HTTP. Like a repository, but for another service instead of a database.
 * (Replaces the legacy RestTemplate, which is now in maintenance mode.)
 */
@Component
public class CouponClient {

    private final RestClient restClient;

    public CouponClient(@Value("${app.coupon-service.url}") String couponServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(couponServiceUrl)
                .build();
    }

    // GET http://localhost:8081/api/coupons/{id}  ->  the coupon, or empty if coupon-service says 404
    public Optional<CouponDto> findCoupon(String couponId) {
        try {
            CouponDto coupon = restClient.get()
                    .uri("/api/coupons/{id}", couponId)
                    .retrieve()               // send the request; throws on 4xx/5xx responses
                    .body(CouponDto.class);   // turn the JSON response into a CouponDto
            return Optional.ofNullable(coupon);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
