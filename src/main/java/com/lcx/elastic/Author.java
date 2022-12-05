package com.lcx.elastic;

import lombok.Data;

/**
 * Create by LCX on 8/14/2022 10:06 PM
 */
@Data
public class Author {
    private String name;

    public Author(String name) {
        this.name = name;
    }
}
