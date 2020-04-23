package com.sungwony.coupon.springboot.service;

import com.sungwony.coupon.springboot.domain.coupon.*;
import com.sungwony.coupon.springboot.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public void generateCoupon(int numOfCoupon){
        int count = 0;
        while(count < numOfCoupon) {
            Coupon coupon = Coupon.generateCoupon();
            Coupon findCoupon = couponRepository.findByCode(coupon.getCode()).orElse(null);
            if (findCoupon == null) {
                count ++;
                couponRepository.save(coupon);
            }
        }
    }

    @Transactional
    public Coupon issueCoupon(User user){
        Coupon coupon = couponRepository.findOneOfUnusedCoupon()
                            .orElseThrow(() -> new NoAvailableCouponException(CouponError.NO_AVAILABLE_COUPON));
        coupon.setStatus(CouponStatus.ISSUED);
        coupon.setUser(user);
        return coupon;
    }

    @Transactional(readOnly = true)
    public List<Coupon> findIssuedCouponListByUserId(User user){
        return couponRepository.findIssuedCouponListByUserId(user);
    }

    @Transactional
    public void useCoupon(String code, User user){
        Coupon coupon = couponRepository.findByCodeAndStatusNot(code, CouponStatus.CREATED)
                            .orElseThrow(() -> new NoAvailableCouponException(CouponError.NON_EXISTENT));

        if(coupon.getStatus() == CouponStatus.EXPIRED ||
                coupon.getExpireDate().isBefore(LocalDate.now()))
            throw new NoAvailableCouponException(CouponError.EXPIRED);
        else if(coupon.getStatus() == CouponStatus.USED)
            throw new NoAvailableCouponException(CouponError.ALREADY_USED);
        else if(!user.getId().equals(coupon.getUser().getId()))
            throw new NoAvailableCouponException(CouponError.NO_GRANT);

        coupon.setStatus(CouponStatus.USED);
        coupon.setUser(user);
    }

    @Transactional
    public void cancelCoupon(String code, User user){
        Coupon coupon = couponRepository.findByCodeAndStatusNot(code, CouponStatus.CREATED)
                            .orElseThrow(() -> new NoAvailableCouponException(CouponError.NON_EXISTENT));

        if(coupon.getStatus() == CouponStatus.EXPIRED ||
                coupon.getExpireDate().isBefore(LocalDate.now()))
            throw new NoAvailableCouponException(CouponError.EXPIRED);
        else if(coupon.getStatus() == CouponStatus.ISSUED)
            throw new NoAvailableCouponException(CouponError.NO_USED);
        else if(!user.getId().equals(coupon.getUser().getId()))
            throw new NoAvailableCouponException(CouponError.NO_GRANT);

        coupon.setStatus(CouponStatus.CANCELED);
    }

    @Transactional(readOnly = true)
    public List<Coupon> findExpiredCouponListByExpiredDate(LocalDate searchDate){

        return couponRepository.findExpiredCouponListByExpireDate(searchDate);
    }

    public void couponExpireWarningNotify(){
        List<Coupon> couponList = couponRepository.findCouponListByExpireDate(LocalDate.now().plusDays(3));
        for(Coupon coupon : couponList){
            log.info("쿠폰이 3일 후 만료됩니다(쿠폰번호 : {})",coupon.getCode());
        }
    }

    @Transactional
    public void couponExpire() {
        List<Coupon> couponList = couponRepository.findByExpireDateLessThan(LocalDate.now());
        for(Coupon coupon : couponList){
            coupon.setStatus(CouponStatus.EXPIRED);
        }
    }
}
