package com.lcx.util;

import com.lcx.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户的信息，用于代替session对象，并线程隔离
 * Create by LCX on 7/21/2022 5:03 PM
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
