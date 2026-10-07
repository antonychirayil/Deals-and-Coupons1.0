package com.deals.coupon.repository;

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

    public CouponSearchRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Coupon> search(CouponFilter filter, Pageable pageable) {
        Query query = new Query();

        if (hasText(filter.search())) {
            // Pattern.quote: treat the user's text literally, so "c++" or "Rs." can't break the regex
            Pattern text = Pattern.compile(Pattern.quote(filter.search().trim()), Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("provider").regex(text),
                    Criteria.where("description").regex(text),
                    Criteria.where("category").regex(text)));
        }

        if (hasText(filter.category())) {
            query.addCriteria(Criteria.where("category").is(filter.category()));
        }

        if (!filter.includeExpired()) {
            query.addCriteria(Criteria.where("expiryDate").gte(LocalDate.now()));
        }

        // 1) How many match in total? (needed for "page 3 of 76")
        long total = mongoTemplate.count(query, Coupon.class);

        // 2) Fetch only this page. Adding "id" as a last sort key makes the order stable:
        //    many coupons share an expiry date, and without a tie-breaker they could jump between pages.
        Pageable stablePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort().and(Sort.by("id")));
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
