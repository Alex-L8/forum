package com.lcx.dao.elasticsearch;

import com.lcx.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Create by LCX on 8/14/2022 10:57 PM
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Long> {

}
