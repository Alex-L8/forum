package com.lcx.util;


import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by LCX on 7/22/2022 11:56 PM
 */
@Component
public class SensitiveFitter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFitter.class);

    private static final String REPLACEMENT = "***";

    // 根结点
    private TrieNode rootNode = new TrieNode();

    // 初始化
    @PostConstruct // 当spring容器实例化SensitiveFitter这个Bean（服务启动时）以后，// 也就是调用其构造方法之后，这个init方法就自动被调用
    public void init() {
        // 读文件取提供的敏感字符，程序编译后都会保存在classes包中，也就是类路径中，所以用类加载器来读取
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));


        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树中
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败：" + e.getMessage());
        }

    }

    // 将敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TrieNode temp = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = temp.getSubNode(c);

            if (subNode == null) {
                // 初始化子结点
                subNode = new TrieNode();
                temp.addSubNode(c, subNode);
            }

            //指向子结点，进入下一轮循环
            temp = subNode;
        }
        // 设置结束标识
            temp.setKeywordEnd(true);
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String fitter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 前缀树上的指针 1
        TrieNode curNode = rootNode;

        // 指针2
        int begin = 0;

        // 指针3
        int position = 0;

        // 记录结果
        StringBuilder res = new StringBuilder();
        while (position < text.length()) {
            /*//指针3越界, 说明以text.charAt(begin)开头的词不是敏感词
            if (position == text.length()) {
                res.append(text.charAt(begin));
                begin++;
                continue;
            }*/
            char c = text.charAt(position);
            // 跳过特殊干扰符号
            if (isSymbol(c)) {
                // 若前缀树指针指向根结点，将此符号记入结果中，指针2、3走一步
                if (curNode == rootNode) {
                    res.append(c);
                    begin++;
                }
                // 无论符号在哪，指针3都走一步
                position++;
                continue;
            }

            // 指向当前结点中值为c的子结点
            curNode = curNode.getSubNode(c);
            if (curNode == null) { // 以begin开头到position位置的字符串不是敏感词
                res.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向前缀树的根结点
                curNode = rootNode;
            } else if (curNode.isKeywordEnd()) {
                // 发现敏感词,将begin~position字符串替换掉
                res.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向前缀树的根结点
                curNode = rootNode;
            } else {
                // 继续检查下一个字符
                // 在结尾处的检查让position不越界，这样能依次比较begin后的每个字符组成的串是否为敏感词
                if (position < text.length() - 1) {
                    position++;
                }
            }
        }
        /*// 将最后一批字符记入结果
        res.append(text.substring(begin));*/
        return res.toString();
    }

    private boolean isSymbol(Character c) {
        // 0x2E80 ~ 0x9FFF是东亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树
    private class TrieNode{
        // 关键词结束的标识
        private boolean isKeywordEnd = false;

        // 子节点(key是下级字符，value是下级结点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子结点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子结点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
