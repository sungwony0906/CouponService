package com.sungwony.coupon.springboot.util;

import java.util.UUID;

public class CouponUtils {

    private static char[] codeSet = {
        '0','1','2','3','4','5','6','7','8','9',
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };

    public static String generateCode(){
        char[] code = new char[16];
        for(int i=0; i<16; i++) {
            int index = ((int) (Math.random() * 100)) % (codeSet.length-1);
            code[i] = codeSet[index];
        }
        return String.valueOf(code);
    }
}
