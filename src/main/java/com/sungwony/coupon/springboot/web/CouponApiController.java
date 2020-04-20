package com.sungwony.coupon.springboot.web;

import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import com.sungwony.coupon.springboot.domain.coupon.CouponStatus;
import com.sungwony.coupon.springboot.domain.coupon.NoAvailableCouponException;
import com.sungwony.coupon.springboot.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CouponApiController {

    private final CouponService couponService;

    @PostMapping("/api/coupon")
    public ResponseEntity generateCoupon(@RequestBody int count){
        couponService.generateCoupon(count);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/api/coupon")
    public String issueCoupon() {
        return couponService.issueCoupon().getCode();
    }

    @GetMapping("/api/coupons")
    public List<Coupon> findIssuedCouponList(@RequestParam String status){
        if(status.equals(CouponStatus.ISSUED.toString()))
            return couponService.findIssuedCouponList();
        else if(status.equals(CouponStatus.EXPIRED.toString()))
            return couponService.findExpiredCouponListByExpiredDate(LocalDate.now());

        return null;
    }

    @PutMapping("/api/coupon/{code}")
    public ResponseEntity useCoupon(@PathVariable String code, @RequestBody String status){
        if(status.equals(CouponStatus.USED.toString()))
            couponService.useCoupon(code);
        else if(status.equals(CouponStatus.CANCELED.toString()))
            couponService.cancelCoupon(code);

        return new ResponseEntity(HttpStatus.OK);
    }

    @ExceptionHandler(NoAvailableCouponException.class)
    public String couponExceptionHandler(NoAvailableCouponException e){

        return e.getMessage();
    }
}
