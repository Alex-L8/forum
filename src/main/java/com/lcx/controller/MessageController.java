package com.lcx.controller;


import com.alibaba.fastjson.JSONObject;
import com.lcx.entity.Message;
import com.lcx.entity.Page;
import com.lcx.entity.User;
import com.lcx.service.impl.MessageServiceImpl;
import com.lcx.service.impl.UserServiceImpl;
import com.lcx.util.ForumConstant;
import com.lcx.util.HostHolder;
import com.lcx.util.NiuKeUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lcx
 * @since 2022-07-31
 */
@Controller
public class MessageController implements ForumConstant {

    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 获取会话列表,并使得页面显示时只显示每个会话中最新的一条私信
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        /**
         * 要么判断user是否为空，要么用过滤器阻止非登录访问该页面
         */


        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> conversation = new HashMap<>();
                conversation.put("conversation", message);
                // 保存指定会话的总消息数量
                conversation.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                // 保存指定会话的未读消息数量
                conversation.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                Long targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                conversation.put("target", userService.findUserById(targetId));
                conversations.add(conversation);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询该用户所有的未读消息
        int allUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", allUnreadCount);

        // 查询未读通知数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }

    /**
     * 获取某私信详情
     *
     * @param conversationId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 分页信息
        page.setLimit(5);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/letter/detail/" + conversationId);

        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();

        List<Message> unreadMessages = new ArrayList<>();
        if (letterList != null) {

            for (Message message : letterList) {
                Map<String, Object> letter = new HashMap<>();
                if (message.getToId().equals(hostHolder.getUser().getId()) && message.getStatus() == 0) {
                    unreadMessages.add(message);
                }
                letter.put("letter", message);
                letter.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(letter);
            }
        }
        model.addAttribute("letters", letters);
        // 查询私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        /*Collection<Long> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.updateBatchById(ids);
        }*/
        messageService.updateBatchById(unreadMessages);
        messageService.readMessage(unreadMessages);

        return "/site/letter-detail";
    }

    /**
     * 获取私信目标,供该控制器使用
     *
     * @param conversationId
     * @return
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        Long id0 = Long.parseLong(ids[0]);
        Long id1 = Long.parseLong(ids[0]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    /*private List<Long> getLetterIds(List<Message> letterList) {
        List<Long> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }*/

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {

        User target = userService.findUserByName(toName);
        if (target == null) {
            return NiuKeUtil.getJSONString(1, "目标用户不存在!");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() > message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        messageService.addMessage(message);
        return NiuKeUtil.getJSONString(0, null);
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        // 查询评论类的通知
        Message notice = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        if (notice != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("notice", notice);

            String content = HtmlUtils.htmlUnescape(notice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            
            messageVO.put("user", userService.findUserById((Long) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);

            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", unreadCount);
            model.addAttribute("commentNotice", messageVO);
        }

        // 查询点赞类的通知
        notice = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);

        if (notice != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("notice", notice);

            String content = HtmlUtils.htmlUnescape(notice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Long) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);

            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unreadCount);
            model.addAttribute("likeNotice", messageVO);
        }

        // 查询关注类的通知
        notice = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if (notice != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("notice", notice);

            String content = HtmlUtils.htmlUnescape(notice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Long) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);

            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unreadCount);
            model.addAttribute("followNotice", messageVO);
        }

        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        // 查询未读通知数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNotice(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Long) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知来源
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVOList.add(map);
            }
        }
        model.addAttribute("notices", noticeVOList);

        // 设置已读
        messageService.readMessage(noticeList);

        return "/site/notice-detail";
    }
}
