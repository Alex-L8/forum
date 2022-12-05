package com.lcx.elastic;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Create by LCX on 8/14/2022 10:04 PM
 */
@Data
@Document(indexName = "blog")
public class Article {
    @Id
    private String id;
    private String title;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Author> authors;

    public Article(String title) {
        this.title = title;
    }
}
