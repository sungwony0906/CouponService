package com.sungwony.coupon.springboot.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sungwony.coupon.springboot.domain.user.User;
import com.sungwony.coupon.springboot.domain.user.UserRepository;
import com.sungwony.coupon.springboot.util.JwtUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@NoArgsConstructor
@Slf4j
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String givenToken = request.getHeader(JwtProperties.HEADER_STRING);
        if(givenToken == null || !givenToken.startsWith(JwtProperties.TOKEN_PREFIX)){
            throw new IllegalArgumentException("Token이 존재하지 않습니다.");
        }
        givenToken = givenToken.replace(JwtProperties.TOKEN_PREFIX, "").trim();
        String userId = JWT.require(Algorithm.HMAC256(JwtProperties.SECRET))
                .build()
                .verify(givenToken)
                .getSubject();

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        verifyToken(givenToken, user.getToken());
        return true;
    }

    public void verifyToken(String givenToken, String membersToken){
        log.info("givenToken = [{}]",givenToken);
        log.info("memberToken = [{}]",membersToken);
        if(! givenToken.equals(membersToken))
            throw new IllegalArgumentException("Token이 일치하지 않습니다.");

        jwtUtils.verifyToken(givenToken);
    }
}
