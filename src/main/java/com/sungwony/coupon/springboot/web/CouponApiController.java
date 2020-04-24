package com.sungwony.coupon.springboot.web;

import com.sungwony.coupon.springboot.config.auth.LoginUser;
import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import com.sungwony.coupon.springboot.domain.coupon.CouponStatus;
import com.sungwony.coupon.springboot.domain.coupon.NoAvailableCouponException;
import com.sungwony.coupon.springboot.domain.user.User;
import com.sungwony.coupon.springboot.service.CouponService;
import com.sungwony.coupon.springboot.web.dto.GenerateCouponDto;
import com.sungwony.coupon.springboot.web.dto.StatusCouponRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CouponApiController {

    private final CouponService couponService;

    @PostMapping("/api/coupon")
    public void generateCoupon(@RequestBody GenerateCouponDto generateCouponDto){
        couponService.generateCoupon(generateCouponDto.getCount());
    }

    @GetMapping("/api/coupon")
    public String issueCoupon(@LoginUser User user) {
        return couponService.issueCoupon(user).getCode();
    }

    @GetMapping("/api/coupons/issued")
    public List<Coupon> findIssuedCouponList(@LoginUser User user){
        return couponService.findIssuedCouponListByUserId(user);
    }

    @GetMapping("/api/coupons/expired")
    public List<Coupon> findExpiredCouponList(){
        return couponService.findExpiredCouponListByExpiredDate(LocalDate.now());
    }

    @PutMapping("/api/coupon/{code}")
    public void useOrCancelCoupon(@LoginUser User user, @PathVariable String code, @RequestBody StatusCouponRequestDto statusCouponRequestDto){
        String status = statusCouponRequestDto.getStatus();
        if(status.equals(CouponStatus.USED.toString()))
            couponService.useCoupon(code, user);
        else if(status.equals(CouponStatus.CANCELED.toString()))
            couponService.cancelCoupon(code, user);
    }

    @ExceptionHandler(NoAvailableCouponException.class)
    public String couponExceptionHandler(NoAvailableCouponException e){

        return e.getMessage();
    }
}
