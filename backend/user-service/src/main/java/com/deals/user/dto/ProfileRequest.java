package com.deals.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Both fields are optional; if given, they must be valid
public record ProfileRequest(

        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be exactly 10 digits")
        String phone,

        @Size(max = 50, message = "City must be at most 50 characters")
        String city) {
}
