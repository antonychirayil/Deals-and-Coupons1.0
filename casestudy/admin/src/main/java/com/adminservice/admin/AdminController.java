package com.adminservice.admin;

import com.adminservice.admin.Model.User;
import com.couponservice.coupon.Coupon;
import com.couponservice.coupon.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    RestTemplate restTemplate;

    private CouponRepository couponRepository;

    @RequestMapping("")
    public  String Test(){
        return "helloWorld";
    }

    //--------admin-coupon--------------------------------------------------
    @GetMapping(value = "/listCoupon")
    public List<Coupon> getAllCoupons(){
        return Arrays.asList(restTemplate.getForObject("http://coupons/coupons/list",Coupon[].class));
    }

    @PostMapping(value = "/addCoupon")
    public  String addCoupon(@RequestBody Coupon coupon){
        return  restTemplate.postForObject("http://coupons/coupons/add",coupon,String.class);
    }

    @DeleteMapping(value = "/deleteCoupon/{couponID}")
    public String deleteProduct(@PathVariable String couponID) {
        restTemplate.delete("http://coupons/coupons/delete/{couponID}", couponID, String.class);
        return "Deleted Successfully";
    }

    @PutMapping(value = "/updateCoupon/{couponID}")
    public String updateCoupon(@RequestBody Coupon coupon, @PathVariable String couponID){
        restTemplate.put("http://coupons/coupons/update/{couponID}",coupon,couponID,String.class);
        return "coupon Updated Succesfully";
    }

    //--------admin-user(customer)--------------------------------------------------
    @DeleteMapping(value = "/deleteCustomer/{userID}")
    public String deleteUser(@PathVariable String userID) {
        restTemplate.delete("http://users/users/delete/{userID}", userID, String.class);
        return "Customer Deleted Successfully";
    }

    @PutMapping(value = "/updateCustomer/{userID}")
    public String updateUser(@RequestBody User user, @PathVariable String userID){
        restTemplate.put("http://users/users/update/{userID}",user,userID,String.class);
        return "Customer Updated Successfully";
    }

}
