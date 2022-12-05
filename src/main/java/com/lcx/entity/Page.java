package com.lcx.entity;

/**
 * 封装分页相关的信息
 * Create by LCX on 7/28/2022 11:06 AM
 */
public class Page {
    // 当前页码
    private int current = 1;
    // 显示上限
    private int limit = 10;
    // 数据总数
    private int rows;
    // 查询路径（用于复用分页链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset() {
        // current * limit -limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页码数
     * @return
     */
    public int getTotalPageCount() {
        // rows / limit [+1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom() {
        /*int from = 1;
        if (current >= 2) {
            from = current - 2;
        }
        return from;*/

        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取尾页码
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotalPageCount();
        return to > total ? total : to;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }
}
