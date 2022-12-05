package com.lcx.service.impl;

import com.lcx.util.HostHolder;
import com.lcx.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * Create by LCX on 8/7/2022 6:37 PM
 */
@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /*@Autowired
    private HostHolder hostHolder;*/

    // 点赞
    public void like(long userId, int entityType, Long entityId,Long entityUserId) {
        /*String entityLikeKey = RedisUtil.getEntityLike(entityType, entityId);
        boolean isLike = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isLike) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }*/

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisUtil.getEntityLike(entityType, entityId);
                String userLikeKey = RedisUtil.getUserLikeKey(entityUserId);
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启Redis的事务
                operations.multi();

                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                // 提交事务
                return operations.exec();
            }
        });
    }

    // 查询实体点赞的数量
    public Long findEntityLikeCount(int entityType, Long entityId) {
        String entityLikeKey = RedisUtil.getEntityLike(entityType, entityId);
        // ????size获取指定字符串的长度
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某个对某实体的点赞状态
    public int findEntityLikeStatus(Long userId, int entityType, Long entityId) {
        String entityLikeKey = RedisUtil.getEntityLike(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(Long userId) {
        String userLikeKey = RedisUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
