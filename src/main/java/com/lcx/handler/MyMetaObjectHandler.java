package com.lcx.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lcx.util.NiuKeUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;

/**
 * MP实现插入或修改数据时，自动加入创建时间或修改时间
 * Create by LCX on 7/15/2022 9:56 PM
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime localDateTime = NiuKeUtil.getLocalDateTime();
        this.setFieldValByName("createTime", localDateTime, metaObject);
        this.setFieldValByName("updateTime", localDateTime, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", NiuKeUtil.getLocalDateTime(), metaObject);
    }
}
