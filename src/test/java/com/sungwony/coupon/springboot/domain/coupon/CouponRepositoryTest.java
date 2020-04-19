package com.sungwony.coupon.springboot.domain.coupon;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CouponRepositoryTest {

    @Autowired
    CouponRepository couponRepository;

    @After
    public void cleanup(){
        couponRepository.deleteAll();
    }

    @Test
    public void 쿠폰생성(){
        //when
        Coupon coupon = Coupon.generateCoupon();
        coupon = couponRepository.save(coupon);

        //then
        Coupon searchCoupon = couponRepository.findById(coupon.getId()).orElse(null);
        assertThat(coupon.getId()).isEqualTo(coupon.getId());
        assertThat(coupon.getCode()).isEqualTo(coupon.getCode());
    }
}
