package com.lcx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.kafka.annotation.EnableKafka;

import javax.annotation.PostConstruct;

@SpringBootApplication
//@EnableKafka
//@ComponentScans({@ComponentScan("mapper"),@ComponentScan("com.lcx.*")})
//@MapperScan("com.lcx.dao") // 替代这个包下所有...Mapper接口上的@Mapper注解
public class ForumNiukeApplication {

    @PostConstruct
    public void init() {
        // 解决netty启动冲突问题
        // see Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(ForumNiukeApplication.class, args);
    }

}
