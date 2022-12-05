package com.lcx.dao;

import com.lcx.entity.DiscussPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lcx
 * @since 2022-07-27
 */
@Repository
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {

    List<DiscussPost> selectDiscussPosts(@Param("userId")Long userId, int offset, int limit);

    // @Param注解用于给参数取别名或者
    // 如果在动态 SQL 中使用参数作为变量，需要 @Param 注解，即使你只有一个参数
    Integer selectDiscussPostRows(@Param("userId") Long userId);

    DiscussPost selectDiscussPostById(Long id);



}
