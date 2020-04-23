package com.sungwony.coupon.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sungwony.coupon.springboot.config.JwtProperties;
import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import com.sungwony.coupon.springboot.domain.coupon.CouponError;
import com.sungwony.coupon.springboot.domain.coupon.CouponRepository;
import com.sungwony.coupon.springboot.domain.coupon.CouponStatus;
import com.sungwony.coupon.springboot.domain.user.User;
import com.sungwony.coupon.springboot.domain.user.UserRepository;
import com.sungwony.coupon.springboot.util.JwtUtils;
import com.sungwony.coupon.springboot.util.JwtUtilsImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    private MockMvc mvc;

    private User user;

    @Before
    public void setupUser() throws Exception{
        user = User.builder()
                .userId("sungwon")
                .password("password")
                .email("email@gmail.com")
                .name("sungwon")
                .token(jwtUtils.createToken("sungwon"))
                .build();

        userRepository.save(user);
    }

    @Before
    public void setup() throws Exception{
        mvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .build();
    }

    @After
    public void tearDown() throws Exception{
        couponRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void Coupon_생성된다() throws Exception{
        //given
        int count = 10;
        String url ="http://localhost:"+port+"/api/coupon";

        //when
        mvc.perform(post(url)
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(String.valueOf(count)))
                .andExpect(status().isOk());

        //then
        List<Coupon> couponList = couponRepository.findAll();
        assertThat(couponList.size()).isEqualTo(count);
    }

    @Test
    public void Coupon_지급된다() throws Exception{
        //given
        Coupon coupon = Coupon.generateCoupon();
        couponRepository.save(coupon);
        String url = "http://localhost:"+port+"/api/coupon";

        //when
        MvcResult result = mvc.perform(get(url)
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        String code = result.getResponse().getContentAsString();
        Coupon searchCoupon = couponRepository.findAll().get(0);
        assertThat(code).isEqualTo(searchCoupon.getCode());
    }

    @Test
    public void 지급된_Coupon_조회() throws Exception{
        //given
        String url = "http://localhost:"+port+"/api/coupons?status="+CouponStatus.ISSUED.toString();
        int count = 2;
        for(int i=0; i<count; i++) {
            Coupon coupon = Coupon.generateCoupon();
            coupon.setStatus(CouponStatus.ISSUED);
            coupon = couponRepository.save(coupon);
        }

        Coupon coupon3 = Coupon.generateCoupon();
        coupon3 = couponRepository.save(coupon3);

        //when
        MvcResult result = mvc.perform(get(url)
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Coupon> couponList = Arrays.asList(mapper.readValue(json, Coupon[].class));

        //then
        assertThat(couponList.size()).isEqualTo(count);
        for(int i=0; i<couponList.size(); i++)
            assertThat(couponList.get(i).getStatus()).isEqualTo(CouponStatus.ISSUED);
    }

    @Test
    public void 지급된_Coupon_사용() throws Exception{
        //given
        String url = "http://localhost"+port+"/api/coupon/";
        Coupon issuedCoupon = Coupon.generateCoupon();
        issuedCoupon.setStatus(CouponStatus.ISSUED);
        issuedCoupon = couponRepository.save(issuedCoupon);

        //when
        mvc.perform(put(url+issuedCoupon.getCode())
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CouponStatus.USED.toString()))
                .andExpect(status().isOk());

        //then
        Coupon findCoupon = couponRepository.findByCode(issuedCoupon.getCode()).orElse(null);
        assertThat(findCoupon.getStatus()).isEqualTo(CouponStatus.USED);

        //given
        Coupon createdCoupon = Coupon.generateCoupon();
        createdCoupon = couponRepository.save(createdCoupon);

        //when
        MvcResult resultCreated = mvc.perform(put(url+createdCoupon.getCode())
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CouponStatus.USED.toString()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(resultCreated.getResponse().getContentAsString()).isEqualTo(CouponError.NON_EXISTENT.message);

        //given
        Coupon usedCoupon = Coupon.generateCoupon();
        usedCoupon.setStatus(CouponStatus.USED);
        usedCoupon = couponRepository.save(usedCoupon);

        //when
        MvcResult resultUsed = mvc.perform(put(url+usedCoupon.getCode())
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CouponStatus.USED.toString()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(resultUsed.getResponse().getContentAsString()).isEqualTo(CouponError.ALREADY_USED.message);

        //given
        Coupon expiredCoupon = Coupon.generateCoupon();
        expiredCoupon.setStatus(CouponStatus.EXPIRED);
        expiredCoupon = couponRepository.save(expiredCoupon);

        //when
        MvcResult resultExpire = mvc.perform(put(url+expiredCoupon.getCode())
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CouponStatus.USED.toString()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(resultExpire.getResponse().getContentAsString()).isEqualTo(CouponError.EXPIRED.message);

    }

    @Test
    public void 지급된_Coupon_사용취소() throws Exception{
        //given
        String url = "http://localhost"+port+"/api/coupon/";
        Coupon usedCoupon = Coupon.generateCoupon();
        usedCoupon.setStatus(CouponStatus.USED);
        usedCoupon.setUser(user);
        usedCoupon = couponRepository.save(usedCoupon);

        //when
        mvc.perform(put(url+usedCoupon.getCode())
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CouponStatus.CANCELED.toString()))
                .andExpect(status().isOk());
        Coupon searchCoupon = couponRepository.findByCode(usedCoupon.getCode()).orElse(null);

        //then
        assertThat(searchCoupon.getStatus()).isEqualTo(CouponStatus.CANCELED);

        //given
        Coupon createdCoupon = Coupon.generateCoupon();
        createdCoupon = couponRepository.save(createdCoupon);

        //when
        MvcResult resultCreated = mvc.perform(put(url+createdCoupon.getCode())
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CouponStatus.CANCELED.toString()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(resultCreated.getResponse().getContentAsString()).isEqualTo(CouponError.NON_EXISTENT.message);

        //given
        Coupon issuedCoupon = Coupon.generateCoupon();
        issuedCoupon.setStatus(CouponStatus.ISSUED);
        issuedCoupon = couponRepository.save(issuedCoupon);

        //when
        MvcResult resultIssued = mvc.perform(put(url+issuedCoupon.getCode())
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CouponStatus.CANCELED.toString()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(resultIssued.getResponse().getContentAsString()).isEqualTo(CouponError.NO_USED.message);

        //given
        Coupon expiredCoupon = Coupon.generateCoupon();
        expiredCoupon.setStatus(CouponStatus.EXPIRED);
        expiredCoupon = couponRepository.save(expiredCoupon);

        //when
        MvcResult resultExpired = mvc.perform(put(url+expiredCoupon.getCode())
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CouponStatus.CANCELED.toString()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(resultExpired.getResponse().getContentAsString()).isEqualTo(CouponError.EXPIRED.message);
    }

    @Test
    public void 당일_만료된_Coupon_조회() throws Exception{
        //given
        String url = "http://localhost"+port+"/api/coupons?status="+CouponStatus.EXPIRED.toString();
        int count = 3;
        for(int i=0; i<count; i++) {
            Coupon expiredCoupon = Coupon.generateCoupon(LocalDate.now());
            expiredCoupon.setStatus(CouponStatus.EXPIRED);
            couponRepository.save(expiredCoupon);
        }

        couponRepository.save(Coupon.generateCoupon());
        Coupon issuedCoupon = Coupon.generateCoupon();
        issuedCoupon.setStatus(CouponStatus.ISSUED);
        couponRepository.save(issuedCoupon);
        Coupon canceledCoupon = Coupon.generateCoupon();
        canceledCoupon.setStatus(CouponStatus.CANCELED);
        couponRepository.save(canceledCoupon);

        //when
        MvcResult result = mvc.perform(get(url)
                .header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+" "+user.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Coupon> expiredCouponList = Arrays.asList(mapper.readValue(json, Coupon[].class));

        //then
        assertThat(expiredCouponList.size()).isEqualTo(count);
        for(int i=0; i<expiredCouponList.size(); i++) {
            assertThat(expiredCouponList.get(i).getStatus()).isEqualTo(CouponStatus.EXPIRED);
            assertThat(expiredCouponList.get(i).getExpireDate()).isEqualTo(LocalDate.now());
        }
    }

}