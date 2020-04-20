package com.sungwony.coupon.springboot.service;

import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import com.sungwony.coupon.springboot.domain.coupon.CouponRepository;
import com.sungwony.coupon.springboot.domain.coupon.CouponStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    public void notifyForCouponExpire() throws Exception{
        //given
        for(int i=0; i<10; i++) {
            Coupon coupon = Coupon.generateCoupon(LocalDate.now().plusDays(3));
            coupon.setStatus(CouponStatus.ISSUED);
            couponRepository.save(coupon);
        }

        //when
        couponService.notifyForCouponExpire();

        //then
    }
}
