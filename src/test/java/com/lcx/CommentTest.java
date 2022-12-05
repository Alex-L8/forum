package com.lcx;

import com.lcx.dao.CommentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Create by LCX on 7/31/2022 1:25 AM
 */
@SpringBootTest
public class CommentTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    void testMapperSelectSelectCountByEntity() {
        commentMapper.selectCountByEntity(1, 14L);
    }

    @Test
    void test() {
        int a = 10 ,b = 20 , c = 9;
        if (a <= b && a >= c) {
            a = 10;
        }
        System.out.println(a);
    }

}
