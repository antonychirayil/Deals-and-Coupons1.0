package com.deals.coupon.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * What a client sends to create or update a coupon (JSON request body).
 * The annotations are checked when the controller parameter is marked @Valid.
 */
public record CouponRequest(

        @NotBlank(message = "Code is required")
        @Size(min = 3, max = 20, message = "Code must be 3 to 20 characters")
        String code,

        @NotBlank(message = "Provider is required")
        String provider,

        @NotBlank(message = "Category is required")
        String category,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        @NotNull(message = "Discount is required")
        @DecimalMin(value = "0", message = "Discount cannot be negative")
        @DecimalMax(value = "100", message = "Discount cannot be more than 100")
        Double discount,

        @NotNull(message = "Expiry date is required")
        @FutureOrPresent(message = "Expiry date cannot be in the past")
        LocalDate expiryDate) {
}
