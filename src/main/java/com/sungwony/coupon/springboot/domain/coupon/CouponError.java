package com.sungwony.coupon.springboot.domain.coupon;

public enum CouponError {
    EXPIRED("만료된 쿠폰"),
    ALREADY_USED("이미 사용한 쿠폰입니다"),
    NO_AVAILABLE_COUPON("발행 가능한 쿠폰이 없습니다"),
    NO_USED("사용되지 않은 쿠폰입니다"),
    NON_EXISTENT("대상 쿠폰이 존재하지 않습니다"),
    NO_GRANT("쿠폰에 대한 권한이 없습니다.");

    public String message;

    CouponError(String message){
        this.message = message;
    }
}
