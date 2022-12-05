package com.lcx.controller;

import com.lcx.entity.DiscussPost;
import com.lcx.entity.Page;
import com.lcx.service.ElasticsearchService;
import com.lcx.service.impl.LikeService;
import com.lcx.service.impl.UserServiceImpl;
import com.lcx.util.ForumConstant;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by LCX on 8/20/2022 3:01 PM
 */
@Controller
public class SearchController implements ForumConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        Map<String, Object> searchResult =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        List<DiscussPost> discussPosts = (List<DiscussPost>) searchResult.get("discussPosts");

        List<Map<String, Object>> searchVO = new ArrayList<>();
        if (discussPosts != null) {
            for (DiscussPost post : discussPosts) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                searchVO.add(map);
            }
        }
        model.addAttribute("searchVO", searchVO);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(discussPosts == null ? 0 : (Integer) searchResult.get("total"));

        return "/site/search";
    }
}
