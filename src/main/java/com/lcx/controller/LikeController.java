package com.lcx.controller;

import com.lcx.entity.Event;
import com.lcx.entity.User;
import com.lcx.envent.EventProducer;
import com.lcx.service.impl.LikeService;
import com.lcx.util.ForumConstant;
import com.lcx.util.HostHolder;
import com.lcx.util.NiuKeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by LCX on 8/7/2022 8:24 PM
 */
@Controller
public class LikeController implements ForumConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @ResponseBody
    @PostMapping("/like")
    private String like(int entityType, Long entityId, Long entityUserId, Long postId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 数量
        Long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        // if给自己的帖子点赞或者取消赞（包括给自己和别人）则不触发
        if (!entityUserId.equals(user.getId())  && likeStatus==1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }


        return NiuKeUtil.getJSONString(0, null, map);
    }

}
