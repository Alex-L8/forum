package com.lcx.dao;

import com.lcx.entity.LoginTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lcx
 * @since 2022-07-17
 */
//@Mapper
@Repository
@Deprecated
public interface LoginTicketMapper extends BaseMapper<LoginTicket> {

    @Update("update login_ticket set status = #{status} where ticket = #{ticket}")
    int updateStatus(String ticket,int status);


}
