package com.lcx.service;

import com.lcx.dao.elasticsearch.DiscussPostRepository;
import com.lcx.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by LCX on 8/20/2022 10:48 AM
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticTemplate;

    public void saveDiscussPostToElasticsearch(DiscussPost post) {
        discussRepository.save(post);
    }

    public void deleteDiscussPostFromElasticsearch(Long id) {
        discussRepository.deleteById(id);
    }

    public Map<String,Object> searchDiscussPost(String keyword, int current, int limit) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> search = elasticTemplate.search(nativeSearchQuery, DiscussPost.class);

        // 数据量大的时候还是要用long
        int total = (int) search.getTotalHits();


        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();


        // 保存要返回的帖子信息
        List<DiscussPost> discussPosts = new ArrayList<>();

        for (SearchHit<DiscussPost> searchHit : searchHits) {
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            searchHit.getContent().setTitle(highlightFields.get("title") == null ? searchHit.getContent().getTitle() : highlightFields.get("title").get(0));
            searchHit.getContent().setTitle(highlightFields.get("content") == null ? searchHit.getContent().getTitle() : highlightFields.get("content").get(0));
            discussPosts.add(searchHit.getContent());
        }

        // 处理好后封装好要返回给controller的数据
        Map<String, Object> searchResult = new HashMap<>();
        searchResult.put("discussPosts", discussPosts);
        searchResult.put("total", total);

        return searchResult;
    }



}
