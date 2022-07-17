package com.lcx.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lcx.dao.UserMapper;
import com.lcx.entity.User;
import com.lcx.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcx.util.ForumConstant;
import com.lcx.util.MailClient;
import com.lcx.util.NiuKeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 账号注册邮箱激活
 *  业务流程：
 * 1、用户提交注册信息。
 * 2、写入数据库，此时帐号状态未激活。
 * 3、将用户名密码或其他标识字符加密构造成激活识别码（你也可以叫激活码）。
 * 4、将构造好的激活识别码组成URL发送到用户提交的邮箱。
 * 5、用户登录邮箱并点击URL，进行激活。
 * 6、验证激活识别码，如果正确且未过期则激活帐号。
 * bug:如果恶意提交无法发送的邮箱来批量注册，将导致服务器资源浪费。
 *
 * @author lcx
 * @since 2022-07-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService , ForumConstant {

    @Autowired
    private com.lcx.dao.UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该昵称已存在！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }
        // 注册用户
        // 不要使用BeanUtils等工具类来注入属性，影响性能以及容易出现bug
        user.setSalt(NiuKeUtil.generateUUID().substring(0, 5));
        user.setPassword(NiuKeUtil.md5(user.getPassword()) + user.getSalt());
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(NiuKeUtil.generateUUID());
        // 从牛客网的1001个头像中任意设置一个，%为占位符
        user.setHeaderUrl(String.format("http://images.noecoder/head/%dt.png", new Random().nextInt(1000)));
        // 使用mybatis-plus的自动填充
        /*Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        user.setCreateTime(LocalDateTime.ofInstant(instant, zone));*/
        // 存在的问题：如果在判断该邮箱是否能发送之前就保存，容易被批量无效注册，浪费服务器资源
        // 但如果判断邮箱之后再保存，邮箱中的激活连接则无法携带保存到数据库时才生成的userId
        userMapper.insert(user);
        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/niuke/activation/101/code
        String url = domain + contextPath + "/activation/" + userMapper.selectByEmail(user.getEmail()).getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        // 由thymeleaf的TemplateEngine封装发送内容
        String content = templateEngine.process("/mail/activation", context);

        try {
            mailClient.sendMail(user.getEmail(), "账号激活", content);
        } catch (Exception e) {
            map.put("emailMsg", "邮箱发送失败，请尝试更换邮箱！");
            return map;
        }
        return map;
    }

    public int activation(Long userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            UpdateWrapper<User> userWrapper = new UpdateWrapper<>();
            userMapper.update(user, userWrapper.set("status", 1));
//            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }

    }

}
