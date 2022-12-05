package com.lcx;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

/**
 * Create by LCX on 8/12/2022 12:10 AM
 */
@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaProducer1 kafkaProducer;

    @Test
    void testKafka() {
        System.out.println(666);
        kafkaProducer.sendMessage("test1", "你好");
        kafkaProducer.sendMessage("test1", "在吗");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test1() {
        System.out.println(666);
    }
}

@Component
class KafkaProducer1 {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public KafkaProducer1() {
    }

    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }

}

@Component
class KafkaConsumer1 {
    public KafkaConsumer1() {
    }

    @KafkaListener(topics = {"test1"})
    public void handlerMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}
