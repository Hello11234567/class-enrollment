package com.yujeong.classenrollment.service;

import com.yujeong.classenrollment.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;



import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;

    //1. 유저 생성 성공
    @Test
    void 유저생성_성공() {
        //given
        User user = new User();

        user.setName("테스트 유저");
        user.setEmail("test@test.com");

        //when
        User saved = userService.saveUser(user);

        //then
        assertNotNull(saved.getId());
        assertEquals("테스트 유저", saved.getName());
    }

    //2. 유저 조회 성공
    @Test
    void 유저조회_성공() {
        //given
        User user = new User();

        user.setName("테스트 유저");
        user.setEmail("test@test.com");
        User saved = userService.saveUser(user);

        //when
        User searched = userService.getUser(saved.getId());

        //then
        assertNotNull(saved.getId());
        assertEquals("test@test.com", searched.getEmail());
    }

    //3. 없는 유저 조회 실패
    @Test
    void 없는유저조회_실패() {
        //given
        Long 잘못된유저 = 9999L;

        //when + then
        assertThrows(RuntimeException.class, () -> {
            userService.getUser(잘못된유저);
        });
    }
}