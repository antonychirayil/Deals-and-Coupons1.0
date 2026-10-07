package com.deals.coupon.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * One coupon, stored as one document in the MongoDB "coupons" collection.
 * Only the service and repository layers use this class; the API uses the DTOs.
 */
@Document(collection = "coupons")
public class Coupon {

    @Id
    private String id; // MongoDB generates it on first save

    @Indexed(unique = true) // MongoDB itself refuses two coupons with the same code
    private String code;

    private String provider;
    @Indexed // speeds up filtering by category
    private String category;
    private String description;
    private Double discount;
    @Indexed // speeds up "not expired" filtering and sorting by expiry
    private LocalDate expiryDate;

    // Spring Data needs a no-argument constructor to rebuild objects read from MongoDB
    public Coupon() {
    }

    public Coupon(String code, String provider, String category,
                  String description, Double discount, LocalDate expiryDate) {
        this.code = code;
        this.provider = provider;
        this.category = category;
        this.description = description;
        this.discount = discount;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
