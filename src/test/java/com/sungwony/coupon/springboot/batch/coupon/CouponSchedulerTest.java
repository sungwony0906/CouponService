package com.sungwony.coupon.springboot.batch.coupon;

import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import com.sungwony.coupon.springboot.domain.coupon.CouponRepository;
import com.sungwony.coupon.springboot.domain.coupon.CouponStatus;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CouponSchedulerTest {

    @Autowired
    private CouponScheduler couponScheduler;

    @Autowired
    private CouponRepository couponRepository;

    @After
    public void tearDown() throws Exception{
        couponRepository.deleteAll();
    }

    @Test
    public void Coupon_만료를_알린다() throws Exception{
        for(int i=0; i<10; i++) {
            Coupon coupon = Coupon.generateCoupon(LocalDate.now().plusDays(3));
            coupon.setStatus(CouponStatus.ISSUED);
            couponRepository.save(coupon);
        }
        couponScheduler.invokeNotify();
    }

    @Test
    public void Coupon을_만료한다() throws Exception{
        //given
        for(int i=0; i<10; i++) {
            Coupon coupon = Coupon.generateCoupon(LocalDate.now().minusDays(1));
            coupon.setStatus(CouponStatus.ISSUED);
            couponRepository.save(coupon);
        }

        //when
        couponScheduler.invokeExpire();

        //then
        List<Coupon> couponList = couponRepository.findAll();
        for(Coupon coupon : couponList){
            assertThat(coupon.getExpireDate()).isEqualTo(LocalDate.now().minusDays(1));
            assertThat(coupon.getStatus()).isEqualTo(CouponStatus.EXPIRED);
        }
    }
}
