package com.sungwony.coupon.springboot.util;

public interface JwtUtils {

    public String createToken(String userId);

    public void verifyToken(String givenToken);
}
