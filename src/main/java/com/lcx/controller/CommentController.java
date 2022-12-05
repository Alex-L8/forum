package com.lcx.controller;


import com.lcx.entity.Comment;
import com.lcx.entity.DiscussPost;
import com.lcx.entity.Event;
import com.lcx.envent.EventProducer;
import com.lcx.service.impl.CommentServiceImpl;
import com.lcx.service.impl.DiscussPostServiceImpl;
import com.lcx.util.ForumConstant;
import com.lcx.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lcx
 * @since 2022-07-30
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements ForumConstant {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostServiceImpl discussPostService;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") Long discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);

        // 触发评论事件
        // 如果评论的人为贴主自己,则不触发通知
        Event event = null;
        if (!comment.getUserId().equals(discussPostService.findDiscussPostById(discussPostId).getUserId())) {
            event = new Event()
                    .setTopic(TOPIC_COMMENT)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(comment.getEntityType())
                    .setEntityId(comment.getEntityId())
                    .setData("postId", discussPostId);
            if (comment.getEntityType() == ENTITY_TYPE_POST) { // 评论
                // 目标为帖子
                DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) { // 回复
                // 目标为帖子中的评论
                Comment target = commentService.getById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            }
            eventProducer.fireEvent(event);
        }


        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
