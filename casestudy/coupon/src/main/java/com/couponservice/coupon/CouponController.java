package com.couponservice.coupon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponRepository couponRepository;

    @GetMapping(value = "/list")
    public List<Coupon> getAllCoupons(){
        return couponRepository.findAll();
    }
    @PostMapping(value = "/add")
    public String addCoupon(@RequestBody Coupon coupon){
        couponRepository.save(coupon);
        return "Coupon Added Successfully";
    }
    @DeleteMapping(value = "/delete/{couponID}")
    public String deleteProduct(@PathVariable String couponID) {
       System.out.println("Delete method called");
        couponRepository.deleteById(couponID);
        return "Deleted Successfully";
    }
    @PutMapping(value = "/update/{couponID}")
    public Coupon updateCoupon(@RequestBody Coupon coupon, @PathVariable String couponID){
        return couponRepository.save(coupon);

    }
}
