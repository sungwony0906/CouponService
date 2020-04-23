package com.sungwony.coupon.springboot.web;

import com.sungwony.coupon.springboot.config.JwtProperties;
import com.sungwony.coupon.springboot.service.UserService;
import com.sungwony.coupon.springboot.web.dto.SignInRequestDto;
import com.sungwony.coupon.springboot.web.dto.SignInResponseDto;
import com.sungwony.coupon.springboot.web.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserApiController {

    public final UserService userService;

    @PostMapping("/api/signUp")
    public void signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto, HttpServletResponse response) throws IllegalArgumentException{
        userService.signUp(signUpRequestDto);
    }

    @PostMapping("/api/signIn")
    public SignInResponseDto signIn(@RequestBody @Valid SignInRequestDto signInRequestDto, HttpServletResponse response) throws Exception{
        SignInResponseDto signInResponseDto = userService.signIn(signInRequestDto);
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX +" "+ signInResponseDto.getToken());
        return signInResponseDto;
    }
}
