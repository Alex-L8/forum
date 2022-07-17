package com.lcx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
//@ComponentScans({@ComponentScan("mapper"),@ComponentScan("com.lcx.*")})
//@MapperScan("com.lcx.dao") // 替代这个包下所有...Mapper接口上的@Mapper注解
public class ForumNiukeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumNiukeApplication.class, args);

    }

}
