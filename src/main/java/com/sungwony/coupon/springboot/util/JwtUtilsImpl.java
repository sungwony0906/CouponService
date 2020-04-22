package com.sungwony.coupon.springboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.sungwony.coupon.springboot.config.JwtProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtilsImpl implements JwtUtils{

    @Override
    public String createToken(String userId) {
        return JWT.create()
                .withSubject(userId)
                .withIssuer(JwtProperties.ISSUER)
                .withExpiresAt(JwtProperties.EXPIRED_TIME)
                .sign(Algorithm.HMAC256(JwtProperties.SECRET));
    }

    @Override
    public void verifyToken(String givenToken) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JwtProperties.SECRET))
                .withIssuer(JwtProperties.ISSUER)
                .build();

        verifier.verify(givenToken);
    }
}
