package com.sungwony.coupon.springboot.domain.coupon;

import com.sungwony.coupon.springboot.domain.BaseTimeEntity;
import com.sungwony.coupon.springboot.domain.user.User;
import com.sungwony.coupon.springboot.util.CouponUtils;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="coupon_id")
    private Long id;

    @Column(length = 16, nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDate expireDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private CouponStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupons")
    @Setter
    private User user;


    @Builder
    private Coupon(String code, LocalDate expireDate, CouponStatus status){
        this.code = code;
        this.expireDate = expireDate;
        this.status = status;
    }

    public static Coupon generateCoupon(){

        return Coupon.builder()
                .code(CouponUtils.generateCode())
                .expireDate(LocalDate.now().plusMonths(3))
                .status(CouponStatus.CREATED)
                .build();
    }
}
