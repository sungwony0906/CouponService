package com.sungwony.coupon.springboot.web.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class SignInRequestDto {
    @NotNull
    private String userId;
    @NotNull
    private String password;
}
