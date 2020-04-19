package com.sungwony.coupon.springboot.service;

import com.sungwony.coupon.springboot.domain.coupon.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public void generateCoupon(int numOfCoupon){
        for(int i=0; i<numOfCoupon; i++) {
            Coupon coupon = Coupon.generateCoupon();
            Coupon findCoupon = couponRepository.findByCode(coupon.getCode()).orElse(null);
            if (findCoupon == null)
                couponRepository.save(coupon);
        }
    }

    @Transactional
    public Coupon issueCoupon(){
        Coupon coupon = couponRepository.findOneOfUnusedCoupon()
                            .orElseThrow(() -> new NoAvailableCouponException(CouponError.NO_AVAILABLE_COUPON));
        coupon.setStatus(CouponStatus.ISSUED);
        return coupon;
    }

    @Transactional(readOnly = true)
    public List<Coupon> findIssuedCouponList(){

        return couponRepository.findIssuedCouponList();
    }

    @Transactional
    public void useCoupon(String code){
        Coupon coupon = couponRepository.findByCode(code)
                            .orElseThrow(() -> new NoAvailableCouponException(CouponError.NON_EXISTENT));

        if(coupon.getStatus() == CouponStatus.EXPIRED ||
                coupon.getExpireDate().isAfter(LocalDate.now()))
            throw new NoAvailableCouponException(CouponError.EXPIRED);
        else if(coupon.getStatus() == CouponStatus.USED)
            throw new NoAvailableCouponException(CouponError.ALREADY_USED);

        coupon.setStatus(CouponStatus.USED);
    }

    @Transactional
    public void cancelCoupon(String code){
        Coupon coupon = couponRepository.findByCode(code)
                            .orElseThrow(() -> new NoAvailableCouponException(CouponError.NON_EXISTENT));

        if(coupon.getStatus() == CouponStatus.EXPIRED ||
                coupon.getExpireDate().isAfter(LocalDate.now()))
            throw new NoAvailableCouponException(CouponError.EXPIRED);
        else if(coupon.getStatus() == CouponStatus.USED)
            throw new NoAvailableCouponException(CouponError.ALREADY_USED);

        coupon.setStatus(CouponStatus.CANCELED);
    }

    @Transactional(readOnly = true)
    public List<Coupon> findExpiredCouponListByExpiredDate(LocalDate searchDate){

        return couponRepository.findExpiredCouponListByExpiredDate(searchDate);
    }
}
