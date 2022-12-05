package com.lcx.controller;


import com.lcx.entity.*;
import com.lcx.envent.EventProducer;
import com.lcx.service.impl.CommentServiceImpl;
import com.lcx.service.impl.DiscussPostServiceImpl;
import com.lcx.service.impl.LikeService;
import com.lcx.service.impl.UserServiceImpl;
import com.lcx.util.ForumConstant;
import com.lcx.util.HostHolder;
import com.lcx.util.NiuKeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lcx
 * @since 2022-07-27
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements ForumConstant {

    @Autowired
    private DiscussPostServiceImpl discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return NiuKeUtil.getJSONString(403, "你还没有登录哦！");
        }
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return NiuKeUtil.getJSONString(405, "标题或内容不能为空！");
        }
        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(user.getId());
        discussPostService.addDiscussPost(post);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        // 报错的情况，将来统一处理
        return NiuKeUtil.getJSONString(0, "发布成功！");
    }

    /**
     * 评论分为帖子的一级评论和一级评论的回复，在一级评论中回复某个人(这个人评论了当前这条一级评论)
     * 显示某帖子的详细信息
     * 向model中注入comments -->List<Map<String, Object>> commentVOList;
     * commentVOList是由List<Comment> commentList中的Comment(comment)和通过comment.getUserId()查出来的User(user)组成
     *
     * @param discussPostId
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") Long discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 作者
        User user = userService.getById(post.getUserId());
        model.addAttribute("user", user);

        // 点赞数
        Long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        // 当前用户的点赞状态(登录时)
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // 评论分页
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论：给贴子的评论
        // 回复：给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVO = new HashMap<>();
                // 评论
                commentVO.put("comment", comment);
                // 评论者
                commentVO.put("user", userService.getById(comment.getUserId()));
                // 点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount", likeCount);
                // 点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeStatus", likeStatus);
                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), page.getOffset(), page.getLimit());
                // 回复的VO列表
                List<Map<String, Object>> replyVOList = new ArrayList<>();

                if (replyList != null) {
                    // 根据查到的回复列表来构造显示到页面的完成回复的相关信息
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        // 回复
                        replyVO.put("reply", reply);
                        // 回复来源
                        replyVO.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0L ? null : userService.findUserById(reply.getTargetId());
                        replyVO.put("target", target);

                        // 点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeStatus", likeStatus);

                        replyVOList.add(replyVO);
                    }
                }
                commentVO.put("replies", replyVOList);
                // 回复的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);
                commentVOList.add(commentVO);
            }
        }
        model.addAttribute("comments", commentVOList);
        return "/site/discuss-detail";
    }

    /**
     * 置顶该帖子
     * @param id
     * @return
     */
    @PostMapping("/top")
    @ResponseBody
    public String setTop(Long id) {
        discussPostService.updateType(id, 1);
        DiscussPost post = discussPostService.findDiscussPostById(id);

        // 触发帖子置顶事件，更新elasticsearch
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(post.getUserId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        return NiuKeUtil.getJSONString(0, "已置顶！");
    }

    /**
     * 加精该帖子
     * @param id
     * @return
     */
    @PostMapping("/highlight")
    @ResponseBody
    public String setHighlight(Long id) {
        discussPostService.updateStatus(id, 1);
        DiscussPost post = discussPostService.findDiscussPostById(id);

        // 触发帖子加精事件，更新elasticsearch
        Event event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                .setUserId(post.getUserId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        return NiuKeUtil.getJSONString(0, "已加精！");
    }

    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(Long id) {
        discussPostService.updateStatus(id, 2);
        DiscussPost post = discussPostService.findDiscussPostById(id);


        // 触发帖子删除事件，更新elasticsearch
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(post.getUserId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        return NiuKeUtil.getJSONString(0, "已删除！");
    }
}
