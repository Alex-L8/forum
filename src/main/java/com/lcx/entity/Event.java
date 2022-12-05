package com.lcx.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by LCX on 8/12/2022 9:54 AM
 */

public class Event {
    private String topic;
    private Long userId;
    private int entityType;
    private Long entityId;
    private Long entityUserId;
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Event setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Event setEntityId(Long entityId) {
        this.entityId = entityId;
        return this;
    }

    public Long getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(Long entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }


}
