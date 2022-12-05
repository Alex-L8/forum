package com.lcx.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lcx.entity.Message;
import com.lcx.dao.MessageMapper;
import com.lcx.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcx.util.HostHolder;
import com.lcx.util.SensitiveFitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lcx
 * @since 2022-07-31
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFitter sensitiveFitter;

    @Autowired
    private HostHolder hostHolder;

    public List<Message> findConversations(Long userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    // 查询当前用户的会话数量
    public int findConversationCount(Long userId) {
        return messageMapper.selectConversationCount(userId);
    }

    // 查询某个会话包含的私信列表
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    // 查询某个会话所包含的私信数量
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    // 查询未读的私信数量
    public int findLetterUnreadCount(Long userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    /**
     * 添加要发送的消息
     * @param message
     * @return
     */
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFitter.fitter(message.getContent()));

        return messageMapper.insert(message);
    }

    /**
     * 用MP来修改未读消息的状态
     * 感觉效率不佳，每条数据的更新都要创建一个新的wrapper对象,占用大量资源
     * @param messageList
     * @return
     */
    public int readMessage(List<Message> messageList) {
        int res = 0;
        for (Message message : messageList) {
            UpdateWrapper<Message> wrapper = new UpdateWrapper<>();
            wrapper.set("status", 1);
            wrapper.eq("id", message.getId());
            res += messageMapper.update(message, wrapper);
        }
        return res;
    }

    /*@Override
    public boolean updateBatchById(Collection<Message> entityList) {
        UpdateWrapper<Message> wrapper = new UpdateWrapper<>();
        wrapper.set("status", 1);
        for (Message message : entityList) {
            wrapper.eq("id", message.getId());
            messageMapper.update(message, wrapper);
        }
        return true;
    }*/

    public Message findLatestNotice(Long userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(Long userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    public int findNoticeUnreadCount(Long userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    public List<Message> findNotices(Long userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
