package com.deals.coupon.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * One page of results plus what the frontend needs to draw page buttons.
 * <T> is a "type parameter": the same record works for a page of coupons, users, anything.
 *
 * Example JSON: { "content": [ ...12 coupons... ], "page": 0, "size": 12, "totalElements": 905, "totalPages": 76 }
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
