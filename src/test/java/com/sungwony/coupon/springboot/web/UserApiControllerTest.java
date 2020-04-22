package com.sungwony.coupon.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sungwony.coupon.springboot.domain.user.User;
import com.sungwony.coupon.springboot.domain.user.UserRepository;
import com.sungwony.coupon.springboot.util.JwtUtils;
import com.sungwony.coupon.springboot.web.dto.SignInRequestDto;
import com.sungwony.coupon.springboot.web.dto.SignInResponseDto;
import com.sungwony.coupon.springboot.web.dto.SignUpRequestDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class UserApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    private JwtUtils jwtUtils;

    private MockMvc mvc;

    @Before
    public void setup() throws Exception{
        mvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .alwaysDo(print())
                .build();
    }

    @After
    public void tearDown() throws Exception{
        userRepository.deleteAll();
    }

    @Test
    public void User_회원가입() throws Exception{
        //given
        String url = "http://localhost:"+port+"/api/signUp";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId("userId")
                .password("password")
                .name("gildong")
                .email("gildong@gmail.com")
                .build();

        //when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(signUpRequestDto)))
                .andExpect(status().isOk());

        //then
        User user = userRepository.findByUserId("userId").orElse(null);
        assertThat(user.getPassword()).isEqualTo(signUpRequestDto.getPassword());
        assertThat(user.getName()).isEqualTo(signUpRequestDto.getName());
        assertThat(user.getEmail()).isEqualTo(signUpRequestDto.getEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void User_중복가입시_IllegalArgumentException() throws Exception{
        //given
        String url = "http://localhost:"+port+"/api/signUp";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId("userId")
                .password("password")
                .name("gildong")
                .email("gildong@gmail.com")
                .build();
        User user = userRepository.save(signUpRequestDto.toEntity());

        //when
        try {
            mvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(new ObjectMapper().writeValueAsString(signUpRequestDto)));
        } catch (NestedServletException e){
            throw (Exception) e.getCause();
        }
    }

    @Test
    public void User_로그인시_토큰반환() throws Exception{
        //given
        String url = "http://localhost:"+port+"/api/signIn";
        User user = userRepository.save(User.builder()
                .userId("userId")
                .password("password")
                .name("name")
                .token(jwtUtils.createToken("userId"))
                .build());
        userRepository.save(user);

        //when
        MvcResult mvcResult = mvc.perform(get(url)
                .param("userId","userId")
                .param("password","password")
        ).andExpect(status().isOk())
                .andReturn();

        SignInResponseDto signInResponseDto = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), SignInResponseDto.class);
        assertThat(signInResponseDto.getToken()).isNotEmpty();
        log.info("token : {}",signInResponseDto.getToken());
    }
}
