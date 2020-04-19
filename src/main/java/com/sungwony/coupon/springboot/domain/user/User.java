package com.sungwony.coupon.springboot.domain.user;

import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String userId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Coupon> couponList;

    @Builder
    public User(String userId, String name, String email){
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}