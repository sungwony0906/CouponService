package com.sungwony.coupon.springboot.web;

import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import com.sungwony.coupon.springboot.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CouponApiController {

    private final CouponService couponService;

    @PostMapping("/api/coupon")
    public ResponseEntity generateCoupon(@RequestBody int count){
        couponService.generateCoupon(count);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/api/coupon")
    public String issueCoupon(){
        return couponService.issueCoupon().getCode();
    }

    @GetMapping("/api/issued-coupons")
    public List<Coupon> findIssuedCouponList(){
        return couponService.findIssuedCouponList();
    }

    @PutMapping("/api/coupon/{code}")
    public ResponseEntity useCoupon(@PathVariable String code){
        couponService.useCoupon(code);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/api/used-coupon/{code}")
    public ResponseEntity cancelCoupon(@PathVariable String code){
        couponService.cancelCoupon(code);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/api/expired-coupon")
    public List<Coupon> findExpiredCouponListByExpiredDate(){
        return couponService.findExpiredCouponListByExpiredDate(LocalDate.now());
    }
}
