package com.deals.coupon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import com.deals.coupon.dto.CouponFilter;
import com.deals.coupon.entity.Coupon;

/**
 * Integration test: runs the REAL MongoDB queries (a mock can't tell us if a query is right).
 * @DataMongoTest starts only the MongoDB part of Spring - no web layer, no services.
 * Needs Docker's MongoDB running. Uses a separate "deals_test" database, never your real data.
 */
@DataMongoTest
@TestPropertySource(properties =
        "spring.mongodb.uri=mongodb://${MONGO_ROOT_USERNAME}:${MONGO_ROOT_PASSWORD}@localhost:27017/deals_test?authSource=admin")
class CouponSearchRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        // Safety check BEFORE deleting anything: we must be on the test database
        assertEquals("deals_test", mongoTemplate.getDb().getName());
        couponRepository.deleteAll();

        couponRepository.saveAll(List.of(
                new Coupon("SWIG50", "Swiggy", "Food", "50% off your first order", 50.0, today.plusDays(5)),
                new Coupon("ZOM20", "Zomato", "Food", "20% off on orders above Rs. 499", 20.0, today.plusDays(30)),
                new Coupon("AMZ10", "Amazon", "Electronics", "10% off headphones", 10.0, today.plusDays(10)),
                new Coupon("AMZOLD", "Amazon", "Electronics", "Old deal", 70.0, today.minusDays(3)),
                new Coupon("TODAY5", "Myntra", "Fashion", "Ends today", 5.0, today)));
    }

    @Test
    void search_withoutFilters_hidesExpiredAndSortsBySoonestExpiry() {
        Page<Coupon> page = couponRepository.search(new CouponFilter(null, null, false),
                PageRequest.of(0, 10, Sort.by("expiryDate")));

        assertEquals(4, page.getTotalElements()); // AMZOLD is expired; TODAY5 is still valid today
        assertEquals(List.of("TODAY5", "SWIG50", "AMZ10", "ZOM20"), codes(page));
    }

    @Test
    void search_includeExpired_returnsEverything() {
        Page<Coupon> page = couponRepository.search(new CouponFilter(null, null, true), PageRequest.of(0, 10));

        assertEquals(5, page.getTotalElements());
    }

    @Test
    void search_text_ignoresCaseAndLooksInProviderAndDescription() {
        assertEquals(List.of("SWIG50"), codes(search("SWIGGY")));       // provider, different case
        assertEquals(List.of("AMZ10"), codes(search("headphones")));   // description
        assertEquals(List.of("ZOM20"), codes(search("rs. 499")));       // "." is matched literally, not as a regex
    }

    @Test
    void search_category_filtersExactly() {
        Page<Coupon> page = couponRepository.search(new CouponFilter(null, "Food", false),
                PageRequest.of(0, 10, Sort.by("discount").descending()));

        assertEquals(List.of("SWIG50", "ZOM20"), codes(page));
    }

    @Test
    void search_paging_returnsOnePageAndTheTotal() {
        Page<Coupon> secondPage = couponRepository.search(new CouponFilter(null, null, false),
                PageRequest.of(1, 3, Sort.by("expiryDate")));

        assertEquals(4, secondPage.getTotalElements());
        assertEquals(2, secondPage.getTotalPages());
        assertEquals(List.of("ZOM20"), codes(secondPage)); // only 1 left on page 2
    }

    @Test
    void findAllCategories_returnsDistinctNamesAlphabetically() {
        assertEquals(List.of("Electronics", "Fashion", "Food"), couponRepository.findAllCategories());
    }

    private Page<Coupon> search(String text) {
        return couponRepository.search(new CouponFilter(text, null, false), PageRequest.of(0, 10));
    }

    private List<String> codes(Page<Coupon> page) {
        return page.getContent().stream().map(Coupon::getCode).toList();
    }
}
