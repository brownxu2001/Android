package com.example.music_xuzhaocheng.module;

import java.util.List;

public class HomePageData {
    private List<HomePageInfo> records;
    private int total;
    private int size;
    private int current;
    private int pages;

    // getter方法
    public List<HomePageInfo> getRecords() { return records; }
    public int getTotal() { return total; }
    public int getSize() { return size; }
    public int getCurrent() { return current; }
    public int getPages() { return pages; }
}