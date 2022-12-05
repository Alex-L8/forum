package com.lcx.controller;

import com.lcx.entity.DiscussPost;
import com.lcx.entity.Page;
import com.lcx.entity.User;
import com.lcx.service.impl.DiscussPostServiceImpl;
import com.lcx.service.impl.LikeService;
import com.lcx.service.impl.UserServiceImpl;
import com.lcx.util.ForumConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by LCX on 7/28/2022 9:50 AM
 */
@Controller
public class HomeController implements ForumConstant {

    @Autowired
    private DiscussPostServiceImpl discussPostService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private LikeService likeService;

    @GetMapping({"/index","/"})
    public String getIndexPage(Model model, Page page) {
        page.setRows(discussPostService.findDiscussPostRows(0L));
        page.setPath("/index");
        model.addAttribute("page", page);
        List<DiscussPost> list = discussPostService.findDiscussPosts(0L, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.getById(post.getUserId());
                map.put("user", user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

    @GetMapping("/denied")
    public String getDeniedPage() {
        return "/error/404";
    }


}
