package com.deals.coupon.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.deals.coupon.dto.CouponFilter;
import com.deals.coupon.dto.CouponResponse;
import com.deals.coupon.dto.PageResponse;
import com.deals.coupon.exception.CouponNotFoundException;
import com.deals.coupon.service.CouponService;

/**
 * Web layer tests: starts ONLY the controller + error handler (no database).
 * MockMvc sends fake HTTP requests; we check status codes and JSON.
 */
@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // replaces the real CouponService inside Spring with a fake one
    private CouponService couponService;

    private final CouponResponse sampleCoupon = new CouponResponse(
            "abc", "SAVE20", "Amazon", "Electronics", "20% off", 20.0, LocalDate.of(2099, 12, 31), false);

    @Test
    void searchCoupons_returnsOnePageWithPagingInfo() throws Exception {
        when(couponService.searchCoupons(any(), any()))
                .thenReturn(new PageResponse<>(List.of(sampleCoupon), 0, 12, 1, 1));

        mockMvc.perform(get("/api/coupons").param("search", "amazon").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("SAVE20"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void searchCoupons_passesFiltersAndPagingToService() throws Exception {
        when(couponService.searchCoupons(any(), any())).thenReturn(new PageResponse<>(List.of(), 2, 5, 0, 0));

        mockMvc.perform(get("/api/coupons")
                        .param("search", "pizza").param("category", "Food").param("includeExpired", "true")
                        .param("page", "2").param("size", "5").param("sort", "discount,desc"))
                .andExpect(status().isOk());

        // Records compare by their values, so verify() can check the exact filter and paging received
        verify(couponService).searchCoupons(
                new CouponFilter("pizza", "Food", true),
                PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC, "discount")));
    }

    @Test
    void getCouponById_returns404WhenNotFound() throws Exception {
        when(couponService.getCouponById("missing-id")).thenThrow(new CouponNotFoundException("missing-id"));

        mockMvc.perform(get("/api/coupons/missing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Coupon not found with id: missing-id"));
    }

    @Test
    void createCoupon_returns201WhenValid() throws Exception {
        when(couponService.createCoupon(any())).thenReturn(sampleCoupon);

        String body = """
                {
                  "code": "SAVE20",
                  "provider": "Amazon",
                  "category": "Electronics",
                  "description": "20% off",
                  "discount": 20,
                  "expiryDate": "2099-12-31"
                }
                """;

        mockMvc.perform(post("/api/coupons").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc"));
    }

    @Test
    void createCoupon_returns400WhenInvalid() throws Exception {
        String body = """
                {
                  "code": "x",
                  "provider": "",
                  "category": "Electronics",
                  "discount": 150,
                  "expiryDate": "2099-12-31"
                }
                """;

        mockMvc.perform(post("/api/coupons").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").value("Code must be 3 to 20 characters"))
                .andExpect(jsonPath("$.errors.provider").value("Provider is required"))
                .andExpect(jsonPath("$.errors.discount").value("Discount cannot be more than 100"));
    }
}
