package com.lcx.dao;

import com.lcx.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lcx
 * @since 2022-07-30
 */
@Repository
public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> selectCommentsByEntity(int entityType, Long entityId, int offset, int limit);

    int selectCountByEntity(int entityType, Long entityId);

    int updateCommentCount(Long id, int commentCount);

//    Comment selectCo
}
