package com.lcx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拦截器，在方法前标注自定义注解，拦截所有请求，只处理带有该注解的方法
 * 自定义注解
 * 常用的元注解：@Target @Retention @Document @Inherited
 * Create by LCX on 7/22/2022 11:10 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
