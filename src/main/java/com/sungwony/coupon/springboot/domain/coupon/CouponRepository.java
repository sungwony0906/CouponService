package com.sungwony.coupon.springboot.domain.coupon;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query(value = "SELECT * FROM coupon c WHERE c.status = 'CREATED' limit 1", nativeQuery = true)
    public Optional<Coupon> findOneOfUnusedCoupon();

    @Query("SELECT new com.sungwony.coupon.springboot.domain.coupon.Coupon(c.code, c.expireDate, c.status) FROM Coupon c WHERE c.status = 'ISSUED'")
    public List<Coupon> findIssuedCouponList();

    public Optional<Coupon> findByCode(String code);

    public Optional<Coupon> findByCodeAndStatusNot(String code, CouponStatus status);

    @Query("SELECT new com.sungwony.coupon.springboot.domain.coupon.Coupon(c.code, c.expireDate, c.status) FROM Coupon c WHERE c.expireDate = :expireDate AND c.status = 'EXPIRED'")
    public List<Coupon> findExpiredCouponListByExpireDate(LocalDate expireDate);

    @Query("SELECT new com.sungwony.coupon.springboot.domain.coupon.Coupon(c.code, c.expireDate, c.status) FROM Coupon c WHERE c.expireDate = :expireDate AND c.status = 'ISSUED'")
    public List<Coupon> findCouponListByExpireDate(LocalDate expireDate);

    public List<Coupon> findByExpireDateLessThan(LocalDate expireDate);
}
