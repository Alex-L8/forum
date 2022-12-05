package com.lcx;

import com.lcx.dao.DiscussPostMapper;
import com.lcx.dao.elasticsearch.DiscussPostRepository;
import com.lcx.entity.DiscussPost;
import com.lcx.entity.Page;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Create by LCX on 8/14/2022 11:00 PM
 */
@SpringBootTest
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    void testInsertOne() {
        discussRepository.save(discussPostMapper.selectDiscussPostById(1561261388314218498L));
    }

    @Test
    void testSearchByRepository() {
        /*SearchHits<DiscussPost> search = template.search(Query.findAll(), DiscussPost.class);
        Iterator<SearchHit<DiscussPost>> iterator = search.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }*/

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(1, 1))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> search = template.search(nativeSearchQuery, DiscussPost.class);
        long total = search.getTotalHits();
        System.out.println(total+"******");
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        List<DiscussPost> discussPosts = new ArrayList<>();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            searchHit.getContent().setTitle(highlightFields.get("title") == null ? searchHit.getContent().getTitle() : highlightFields.get("title").get(0));
            searchHit.getContent().setTitle(highlightFields.get("content") == null ? searchHit.getContent().getTitle() : highlightFields.get("content").get(0));
            discussPosts.add(searchHit.getContent());
        }
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost.toString());
        }


    }
}
