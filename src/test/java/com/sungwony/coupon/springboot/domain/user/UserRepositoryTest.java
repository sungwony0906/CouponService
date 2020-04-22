package com.sungwony.coupon.springboot.domain.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void 유저생성(){
        //given
        User user = User.builder()
                .userId("dummyId")
                .name("dummy")
                .password("dummyPw")
                .email("dummy@gmail.com")
                .build();

        user = userRepository.save(user);

        //when
        User findUser = userRepository.findByUserId("dummyId").orElse(null);

        //then
        assertThat(user.getId()).isEqualTo(findUser.getId());
        assertThat(user.getName()).isEqualTo(findUser.getName());
    }
}
