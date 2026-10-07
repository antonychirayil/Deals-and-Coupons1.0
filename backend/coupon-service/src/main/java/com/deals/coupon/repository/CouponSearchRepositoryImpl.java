package com.deals.coupon.repository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.deals.coupon.dto.CouponFilter;
import com.deals.coupon.entity.Coupon;

/**
 * Builds the MongoDB query step by step: only the filters that were given are added.
 * MongoTemplate is Spring's lower-level tool for MongoDB, used when repository methods aren't enough.
 */
public class CouponSearchRepositoryImpl implements CouponSearchRepository {

    private final MongoTemplate mongoTemplate;
    private final Clock clock;

    public CouponSearchRepositoryImpl(MongoTemplate mongoTemplate, Clock clock) {
        this.mongoTemplate = mongoTemplate;
        this.clock = clock;
    }

    @Override
    public Page<Coupon> search(CouponFilter filter, Pageable pageable) {
        Query query = new Query();

        // Field names are written as method references (Coupon::getProvider) instead of strings ("provider").
        // If a field is ever renamed, the compiler now catches every query that uses it - a typo in a
        // string would only fail at runtime, by silently matching nothing.

        if (hasText(filter.search())) {
            // Pattern.quote: treat the user's text literally, so "c++" or "Rs." can't break the regex
            Pattern text = Pattern.compile(Pattern.quote(filter.search().trim()), Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where(Coupon::getProvider).regex(text),
                    Criteria.where(Coupon::getDescription).regex(text),
                    Criteria.where(Coupon::getCategory).regex(text)));
        }

        if (hasText(filter.category())) {
            query.addCriteria(Criteria.where(Coupon::getCategory).is(filter.category()));
        }

        if (!filter.includeExpired()) {
            query.addCriteria(Criteria.where(Coupon::getExpiryDate).gte(LocalDate.now(clock)));
        }

        // 1) How many match in total? (needed for "page 3 of 76")
        long total = mongoTemplate.count(query, Coupon.class);

        // 2) Fetch only this page. Adding "id" as a last sort key makes the order stable:
        //    many coupons share an expiry date, and without a tie-breaker they could jump between pages.
        Pageable stablePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort().and(Sort.by(Coupon::getId)));
        List<Coupon> coupons = mongoTemplate.find(query.with(stablePageable), Coupon.class);

        return new PageImpl<>(coupons, pageable, total);
    }

    @Override
    public List<String> findAllCategories() {
        return mongoTemplate.findDistinct(new Query(), "category", Coupon.class, String.class)
                .stream()
                .sorted()
                .toList();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
