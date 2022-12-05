package com.lcx.controller.interceptor;

import com.lcx.entity.User;
import com.lcx.service.DataService;
import com.lcx.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by LCX on 9/28/2022 3:49 PM
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计UV（独立访客）
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 统计DAU（日活跃用户）
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }
        return true;
    }


}
