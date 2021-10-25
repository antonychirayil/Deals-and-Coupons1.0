package com.adminservice.admin.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Coupons")
public class Coupon {

    @Id
    private String id;
    private String provider;
    private String code;
    private String category;
    private String description;

    public Coupon() {
    }

    public Coupon(String id, String provider, String code, String category, String description) {
        super();
        this.id = id;
        this.provider = provider;
        this.code = code;
        this.category = category;
        this.description = description;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
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
}
