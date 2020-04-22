package com.sungwony.coupon.springboot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInResponseDto {
    private String token;

    @Builder
    public SignInResponseDto(String token){
        this.token = token;
    }
}
