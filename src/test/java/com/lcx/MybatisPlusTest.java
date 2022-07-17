package com.lcx;

import com.lcx.dao.UserMapper;
import com.lcx.entity.User;
import com.lcx.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by LCX on 7/15/2022 4:05 PM
 */
@SpringBootTest
@MapperScan("com.lcx.dao")
public class MybatisPlusTest {
    @Autowired
    private UserMapper userMapper;

    @Resource
    private UserServiceImpl userService;


    @Test
    public void deleteById() {
        userMapper.deleteById(1548229668086325249L);
    }


    /**
     * 批量插入
     */
    public void testBatch() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("靓仔" + i);
            user.setEmail("a111"+i+"f@qq.com");
            users.add(user);
        }
        boolean result = userService.saveBatch(users);
        Assertions.assertEquals(true, result);
    }


}
