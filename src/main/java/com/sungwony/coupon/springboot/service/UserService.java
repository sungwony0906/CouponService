package com.sungwony.coupon.springboot.service;

import com.sungwony.coupon.springboot.domain.user.User;
import com.sungwony.coupon.springboot.domain.user.UserRepository;
import com.sungwony.coupon.springboot.util.JwtUtils;
import com.sungwony.coupon.springboot.web.dto.SignInRequestDto;
import com.sungwony.coupon.springboot.web.dto.SignInResponseDto;
import com.sungwony.coupon.springboot.web.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public void signUp(SignUpRequestDto signUpRequestDto) throws IllegalArgumentException{
        verifyDuplicatedUser(signUpRequestDto.getUserId());
        User user = signUpRequestDto.toEntity();
        user.setToken(jwtUtils.createToken(user.getUserId()));
        userRepository.save(user);
    }

    private void verifyDuplicatedUser(String userId) throws IllegalArgumentException{
        if(userRepository.findByUserId(userId).orElse(null) != null)
            throw new IllegalArgumentException("이미 등록된 유저입니다.");
    }

    public SignInResponseDto signIn(SignInRequestDto signInRequestDto){
        User findUser = userRepository.findByUserId(signInRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if(!findUser.getPassword().equals(signInRequestDto.getPassword()))
            throw new IllegalArgumentException("암호가 일치하지 않습니다.");
        return SignInResponseDto.builder().token(findUser.getToken()).build();
    }
}
