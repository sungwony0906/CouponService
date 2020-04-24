package com.sungwony.coupon.springboot.domain.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sungwony.coupon.springboot.domain.BaseTimeEntity;
import com.sungwony.coupon.springboot.domain.user.User;
import com.sungwony.coupon.springboot.util.CouponUtils;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate expireDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private CouponStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    @Setter
    private User user;


    @Builder
    public Coupon(String code, LocalDate expireDate, CouponStatus status) {
        this.code = code;
        this.expireDate = expireDate;
        this.status = status;
    }

    public static Coupon generateCoupon(){

        return generateCoupon(LocalDate.now().plusMonths(3));
    }

    public static Coupon generateCoupon(LocalDate expireDate){

        return Coupon.builder()
                .code(CouponUtils.generateCode())
                .expireDate(expireDate)
                .status(CouponStatus.CREATED)
                .build();
    }
}
