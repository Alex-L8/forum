package com.lcx;

import com.lcx.dao.MessageMapper;
import com.lcx.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Create by LCX on 7/31/2022 5:06 PM
 */
@SpringBootTest
public class MessageTest {
    @Autowired
    private MessageMapper messageMapper;

    @Test
    void test() {
        /*int count = messageMapper.selectConversationCount(111L);
        System.out.println(count);*/

        List<Message> list = messageMapper.selectConversations(111L, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111L);
        System.out.println(count);
        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);


        count = messageMapper.selectLetterUnreadCount(131L, "111_131");
        System.out.println(count);
    }
}
