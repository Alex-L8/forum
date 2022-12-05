package com.lcx.controller;

import com.lcx.entity.Event;
import com.lcx.entity.Page;
import com.lcx.entity.User;
import com.lcx.envent.EventProducer;
import com.lcx.service.impl.FollowService;
import com.lcx.service.impl.UserServiceImpl;
import com.lcx.util.ForumConstant;
import com.lcx.util.HostHolder;
import com.lcx.util.NiuKeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Create by LCX on 8/9/2022 5:04 PM
 */
@Controller
public class FollowController implements ForumConstant{

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, Long entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);// 现在只能关注用户，后续新增其它关注功能还需要进行修改
        eventProducer.fireEvent(event);

        return NiuKeUtil.getJSONString(0, "关注成功！");
    }

    @PostMapping("/unFollow")
    @ResponseBody
    public String unFollow(int entityType, Long entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);

        return NiuKeUtil.getJSONString(0, "已取消关注！");
    }

    /**
     * 查询某个用户的关注列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/following/{userId}")
    public String getFollowing(@PathVariable("userId") Long userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/following/" + userId);
        page.setRows((int) followService.findFollowingCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> followingList = followService.findFollowing(userId, page.getOffset(), page.getLimit());
        if (followingList != null) {
            for (Map<String, Object> map : followingList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("followingList", followingList);
        return "/site/following";
    }

    /**
     * 查询某个用户的粉丝
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/follower/{userId}")
    public String getFollower(@PathVariable("userId") Long userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/follower/" + userId);
        page.setRows((int) followService.findFollowerCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> followerList = followService.findFollower(userId, page.getOffset(), page.getLimit());
        if (followerList != null) {
            for (Map<String, Object> map : followerList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("followerList", followerList);
        return "/site/follower";
    }

    public boolean hasFollowed(Long userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
