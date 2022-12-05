package com.lcx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Create by LCX on 7/17/2022 10:20 PM
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig {
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo()) // 用来展示该 API 的基本信息
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lcx.controller")) // 设置扫描路径
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("forum-niuke")
                .description("niuke APIs")
                .termsOfServiceUrl("http://localhost:8080/")
                .contact(new Contact("ambrose", "swagger.example", "123@456.com"))
                .version("1.0")
                .build();
    }
}
