// HomePageData.java
package com.example.music_xuzhaocheng.module;

import java.util.List;

public class HomePageData {
    private List<HomePageInfo> records; // 音乐列表
    private int total; // 总数
    private int size; // 当前页大小
    private int current; // 当前页
    private int pages; // 总页数（第3张图补充）

    // getter方法
    public List<HomePageInfo> getRecords() { return records; }
    public int getTotal() { return total; }
    public int getSize() { return size; }
    public int getCurrent() { return current; }
    public int getPages() { return pages; }
}