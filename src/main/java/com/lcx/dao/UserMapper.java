package com.lcx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lcx.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Create by LCX on 7/14/2022 3:40 PM
 */

//@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
//    @Select("select * from user where #{id}")

    User selectByName(String username);

    User selectByEmail(String email);

//    int insertUser(User user);

//    int updateStatus(Long id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
