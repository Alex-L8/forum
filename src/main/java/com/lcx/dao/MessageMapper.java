package com.lcx.dao;

import com.lcx.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lcx
 * @since 2022-07-31
 */
@Repository
public interface MessageMapper extends BaseMapper<Message> {

    // 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    List<Message> selectConversations(Long userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(Long userId);

    // 查询某个会话包含的所有私信
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读的私信数量
    int selectLetterUnreadCount(Long userId, String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 修改私信状态
    int updateStatus(List<Long> ids, int status);

    // 查询某个主题下最新的通知
    Message selectLatestNotice(Long userId, String topic);

    // 查询某个主题所包含的通知数量
    int selectNoticeCount(Long userId, String topic);

    // 查询未读的通知的数量
    int selectNoticeUnreadCount(Long userId, String topic);

    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(Long userId, String topic, int offset, int limit);
}
