package com.lcx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lcx.dao.CommentMapper;
import com.lcx.entity.Comment;
import com.lcx.entity.DiscussPost;
import com.lcx.dao.DiscussPostMapper;
import com.lcx.service.IDiscussPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcx.util.SensitiveFitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lcx
 * @since 2022-07-27
 */
@Service
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper, DiscussPost> implements IDiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFitter sensitiveFitter;

    @Autowired
    private CommentMapper commentMapper;


    /*@PostConstruct
    public void init() {
        Caffeine.new
    }*/

    /**
     * 分页查询帖子
     * @param page
     * @param usrId
     * @return
     */
    public IPage<DiscussPost> findDiscussPosts(IPage<DiscussPost> page, Long usrId) {
        QueryWrapper<DiscussPost> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", usrId);
        return discussPostMapper.selectPage(page, wrapper);
    }

    /**
     * 返回指定范围的帖子
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> findDiscussPosts(Long userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    /**
     * 查询总帖子数
     *
     * @param userId
     * @return
     */
    public Integer findDiscussPostRows(Long userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /*public Long findDiscussPostRows(Long userId) {
        QueryWrapper<DiscussPost> wrapper = new QueryWrapper<>();
        wrapper.ne("status", 3);
        if (userId != 0) {
            wrapper.eq("user_id", userId);
        }
        return discussPostMapper.selectCount(wrapper);
    }*/

    /**
     * 发布帖子
     *
     * @param post
     * @return
     */
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 过滤敏感词
        post.setTitle(sensitiveFitter.fitter(post.getTitle()));
        post.setContent(sensitiveFitter.fitter(post.getContent()));

        return discussPostMapper.insert(post);
    }

    /**
     * 根据帖子id查询整个帖子详情数据
     * @param id
     * @return
     */
    public DiscussPost findDiscussPostById(Long id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     * 修改评论的数量
     * @param id
     * @param commentCount
     * @return
     */
    public int updateCommentCount(Long id, int commentCount) {
        return commentMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(Long id, int type) {
        DiscussPost post = discussPostMapper.selectById(id);
        post.setType(type);
        return discussPostMapper.updateById(post);
    }

    public int updateStatus(Long id, int status) {
        DiscussPost post = discussPostMapper.selectById(id);
        post.setStatus(status);
        return discussPostMapper.updateById(post);
    }




}
