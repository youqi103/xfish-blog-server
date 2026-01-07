package com.youqi.usercenter.service;

import com.youqi.usercenter.model.dto.UserRegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){

    }

    @Test
    void userRegister() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("yupi");
        registerRequest.setPassword("1234");
        registerRequest.setConfirmPassword("1234");
        registerRequest.setEmail("test@example.com");

        userService.userRegister(registerRequest);
        // Expected to fail due to short password

        registerRequest.setUsername("yu");
        userService.userRegister(registerRequest);
        // Expected to fail due to short username

        registerRequest.setUsername("yupi");
        registerRequest.setPassword("123456");
        registerRequest.setConfirmPassword("123456");
        userService.userRegister(registerRequest);
        // Expected to succeed
    }
}