package com.lcx.service.impl;

import com.lcx.entity.User;
import com.lcx.util.ForumConstant;
import com.lcx.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Create by LCX on 8/9/2022 3:46 PM
 */
@Service
public class FollowService implements ForumConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserServiceImpl userService;

    /*@Autowired
    private */

    // 关注某个实体，帖子或用户
    public void follow(Long userId, int entityType, Long entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 关注列表
                String followingKey = RedisUtil.getFollowingKey(userId, entityType);
                // 粉丝列表
                String followerKey = RedisUtil.getFollowerKey(entityId, entityType);

                operations.multi();

                operations.opsForZSet().add(followingKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    // 取消关注
    public void unFollow(Long userId, int entityType, Long entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 关注列表
                String followingKey = RedisUtil.getFollowingKey(userId, entityType);
                // 粉丝列表
                String followerKey = RedisUtil.getFollowerKey(entityId, entityType);

                operations.multi();

                operations.opsForZSet().remove(followingKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    // 查询用户关注的实体数量
    public long findFollowingCount(Long userId, int entityType) {
        // 该用户关注了哪些实体
        String followingKey = RedisUtil.getFollowingKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followingKey);
    }

    // 查询实体的关注者数量
    public long findFollowerCount(Long entityId, int entityType) {
        // 该实体有哪些关注者
        String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否已经关注了该实体
    // 查询对应key在zset中的score是否为null
    public boolean hasFollowed(Long userId, int entityType, Long entityId) {
        String followingKey = RedisUtil.getFollowingKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followingKey, entityId) != null;
    }

    // 查询某用户关注的人
    public List<Map<String, Object>> findFollowing(Long userId, int offset, int limit) {
        String followingKey = RedisUtil.getFollowingKey(userId, ENTITY_TYPE_USER);
        Set<Long> targetIds = redisTemplate.opsForZSet().reverseRange(followingKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Long targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followingKey, targetId);
            map.put("followingTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    // 查询用户的粉丝
    public List<Map<String, Object>> findFollower(Long userId, int offset, int limit) {
        String followerKey = RedisUtil.getFollowerKey(userId, ENTITY_TYPE_USER);
        Set<Long> targetIds = redisTemplate.opsForZSet().range(followerKey, offset, limit);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Long targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followedTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
