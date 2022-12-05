package com.lcx.util;

/**
 * Create by LCX on 8/7/2022 6:26 PM
 */
public class RedisUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWING = "following";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";

    // 单日uv
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    // 区间uv
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    // 单日活跃用户
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    // 区间活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLike(int entityType, Long entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(Long userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // concern:userId:entityType -> zset(entityId,now)
    public static String getFollowingKey(Long userId, int entityType) {
        return PREFIX_FOLLOWING + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的关注者
    // follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(Long entityId, int entityType) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 获取登录凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 获取用户缓存
    public static String getUserKey(Long userId) {
        return PREFIX_USER + SPLIT + userId;
    }

}

