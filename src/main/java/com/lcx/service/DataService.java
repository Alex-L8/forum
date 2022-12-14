package com.lcx.service;

import com.lcx.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Create by LCX on 9/28/2022 2:47 PM
 * UV(Unique Visitor)
 * DAU(Daily Active User
 */
@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    // 将指定的IP记入UV
    public void recordUV(String ip) {
        String redisUV = RedisUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisUV, ip);
    }

    // 统计指定范围内的UV
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String uvKey = RedisUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(uvKey);
            calendar.add(Calendar.DATE, 1);
        }
        // 合并这些数据
        /**
         * redisKey存了合并后的数据,
         * keyList是要合并的目标
         */
        String redisKey = RedisUtil.getUVKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        // 返回统计的结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    // 将指定用户记入DAU，使用bitmap的来记录某个用户是否访问
    // 用户id作为索引，由于索引长度有限制，所以不适合存储位数过长的ID
    public void recordDAU(Long useId) {
        String redisKey = RedisUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, useId, true);
    }

    // 统计指定日期范围内的DAU
    public Long calculateDAU(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String dauKey = RedisUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(dauKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        // 进行OR运算
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisUtil.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));

                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
