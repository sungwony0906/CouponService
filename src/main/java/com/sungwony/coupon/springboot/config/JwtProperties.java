package com.sungwony.coupon.springboot.config;

import java.util.Date;

public class JwtProperties {
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String SECRET = "SUNGWONSECRET0000";
    public static final Date EXPIRED_TIME = new Date(System.currentTimeMillis() + 60*60*100*24*10); // 10 Days
    public static final String ISSUER = "SUNGWON";
    public static final String HEADER_STRING = "Authorization";
}
