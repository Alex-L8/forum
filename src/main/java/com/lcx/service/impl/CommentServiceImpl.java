package com.lcx.service.impl;

import com.lcx.entity.Comment;
import com.lcx.dao.CommentMapper;
import com.lcx.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcx.util.ForumConstant;
import com.lcx.util.SensitiveFitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lcx
 * @since 2022-07-30
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService, ForumConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFitter sensitiveFitter;

    @Autowired
    private DiscussPostServiceImpl discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, Long entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, Long entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 添加过滤后的评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFitter.fitter(comment.getContent()));
        int rows = commentMapper.insert(comment);

        // 更新贴子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }
}
