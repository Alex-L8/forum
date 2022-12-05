package com.lcx;

import com.lcx.util.MailClient;
import com.lcx.util.NiuKeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Create by LCX on 7/15/2022 10:14 PM
 */
@SpringBootTest
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Test
    void testTextMail() {
        mailClient.sendMail("2926506301@qq.com","test","6");
    }

    @Test
    void Md5() {
        String s1 = NiuKeUtil.md5("123");
        System.out.println(s1);
        String salt = NiuKeUtil.generateUUID();
        String p1 = s1 + salt;
        String s2 = NiuKeUtil.md5("123");
        System.out.println(s2);
    }
}
