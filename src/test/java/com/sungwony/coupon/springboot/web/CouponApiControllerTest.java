package com.sungwony.coupon.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sungwony.coupon.springboot.domain.coupon.Coupon;
import com.sungwony.coupon.springboot.domain.coupon.CouponRepository;
import com.sungwony.coupon.springboot.domain.coupon.CouponStatus;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private MockMvc mvc;

    @Before
    public void setup() throws Exception{
        mvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .build();
    }

    @After
    public void tearDown() throws Exception{
        couponRepository.deleteAll();
    }

    @Test
    public void Coupon_생성된다() throws Exception{
        //given
        int count = 10;
        String url ="http://localhost:"+port+"/api/coupon";

        //when
        mvc.perform(post(url)
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
        MvcResult result = mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        String code = result.getResponse().getContentAsString();
        Coupon searchCoupon = couponRepository.findAll().get(0);
        assertThat(code).isEqualTo(searchCoupon.getCode());
    }

    @Test
    public void 지급된_Coupon_조회() throws Exception{
        //given
        Coupon coupon = Coupon.generateCoupon();
        coupon.setStatus(CouponStatus.ISSUED);
        coupon = couponRepository.save(coupon);

        Coupon coupon2 = Coupon.generateCoupon();
        coupon2.setStatus(CouponStatus.ISSUED);
        coupon2 = couponRepository.save(coupon2);

        Coupon coupon3 = Coupon.generateCoupon();
        coupon3 = couponRepository.save(coupon3);

        String url = "http://localhost:"+port+"/api/issued-coupons";

        //when
        MvcResult result = mvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Coupon> couponList = mapper.readValue(json, List.class);

        //then
        assertThat(couponList.size()).isEqualTo(2);
        Coupon issuedCoupon1 = couponRepository.findById(coupon.getId()).orElse(null);
        Coupon issuedCoupon2 = couponRepository.findById(coupon2.getId()).orElse(null);
        assertThat(issuedCoupon1.getStatus()).isEqualTo(CouponStatus.ISSUED);
        assertThat(issuedCoupon1.getCode()).isEqualTo(coupon.getCode());
        assertThat(issuedCoupon2.getStatus()).isEqualTo(CouponStatus.ISSUED);
        assertThat(issuedCoupon2.getCode()).isEqualTo(coupon2.getCode());
    }
}