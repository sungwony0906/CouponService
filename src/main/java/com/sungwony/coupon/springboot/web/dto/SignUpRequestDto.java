package com.sungwony.coupon.springboot.web.dto;

import com.sungwony.coupon.springboot.domain.user.User;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequestDto {
    @NotNull
    private String userId;
    @NotNull
    private String name;
    @NotNull
    private String password;
    private String email;

    @Builder
    public SignUpRequestDto(String userId, String name, String password, String email){
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public User toEntity(){
        return User.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .email(email)
                .build();
    }
}
