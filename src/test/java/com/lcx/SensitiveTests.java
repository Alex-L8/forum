package com.lcx;

import com.lcx.util.SensitiveFitter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Create by LCX on 7/23/2022 1:36 PM
 */
@SpringBootTest
public class SensitiveTests {

    @Autowired
    private SensitiveFitter sensitiveFitter;
    @Test
    void testSensitiveFitter() {
        String text = "/+傻逼，这里可以赌赌博博，可以嫖嫖娼娼，可以杀吸毒人，你个傻逼";
        long begin = System.currentTimeMillis();
        text = sensitiveFitter.fitter(text);
//        text = sensitiveFitter.fitter(text);
        System.out.println(text);
    }
}
