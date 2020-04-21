package com.sungwony.coupon.springboot.batch.coupon;

import com.sungwony.coupon.springboot.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponService couponService;

    @Scheduled(cron = "0 0 11 * * *")
    public void cronCouponExpireNotifyScheduler(){
        couponService.couponExpireWarningNotify();
    }

    @Scheduled(cron = "1 0 * * * *")
    public void cronCouponExpireScheduler(){
        couponService.couponExpire();
    }

    public void invokeNotify(){
        this.cronCouponExpireNotifyScheduler();
    }

    public void invokeExpire(){
        this.cronCouponExpireScheduler();
    }
}
