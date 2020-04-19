package com.sungwony.coupon.springboot.domain.coupon;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query(value = "SELECT * FROM coupon c WHERE c.status = 'CREATED' limit 1", nativeQuery = true)
    public Optional<Coupon> findOneOfUnusedCoupon();

    @Query("SELECT c FROM Coupon c WHERE c.status = 'ISSUED'")
    public List<Coupon> findIssuedCouponList();

    public Optional<Coupon> findByCode(String couponId);

    @Query("SELECT c FROM Coupon c WHERE c.expireDate = :expireDate ")
    public List<Coupon> findExpiredCouponListByExpiredDate(LocalDate expireDate);
}