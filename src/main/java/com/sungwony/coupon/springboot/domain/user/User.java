package com.sungwony.coupon.springboot.domain.user;

import com.sungwony.coupon.springboot.config.auth.StringCryptoConverter;
import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String userId;

    @Column(nullable = false, length = 127)
    private String name;

    @Column(length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    @Convert(converter = StringCryptoConverter.class)
    private String password;

    @Column(length = 255)
    @Setter
    private String token;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Coupon> couponList;

    @Builder
    public User(String userId, String password, String name, String email, String token){
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.token = token;
    }
}