package com.sungwony.coupon.springboot.config.auth.dto;

import com.sungwony.coupon.springboot.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private Long id;
    private String userId;
    private String name;
    private String email;

    public SessionUser(User user){
        this.id = user.getId();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
