package com.sungwony.coupon.springboot.domain.coupon;

import lombok.Getter;

public class NoAvailableCouponException extends RuntimeException{

    @Getter
    private CouponError couponError;

    public NoAvailableCouponException(CouponError couponError) {
        super(couponError.message);
        this.couponError = couponError;
    }
}
