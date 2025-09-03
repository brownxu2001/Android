package com.example.music_xuzhaocheng;

import java.util.List;

public class HomePageResponse {
    private int code;
    private String msg;
    private HomePageData data;

    // Getters
    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public HomePageData getData() { return data; }
}

class HomePageData {
    private List<ModuleInfo> records;
    private int total;
    private int size;
    private int current;
    private int pages;

    // Getters
    public List<ModuleInfo> getRecords() { return records; }
    public int getTotal() { return total; }
    public int getSize() { return size; }
    public int getCurrent() { return current; }
    public int getPages() { return pages; }
}

class ModuleInfo {
    private int moduleConfigId;
    private String moduleName;
    private int style; // 1:banner, 2:横滑大卡, 3:一行一列, 4:一行两列
    private List<MusicInfo> musicInfoList;

    // Getters
    public int getModuleConfigId() { return moduleConfigId; }
    public String getModuleName() { return moduleName; }
    public int getStyle() { return style; }
    public List<MusicInfo> getMusicInfoList() { return musicInfoList; }
}

class MusicInfo {
    private long id;
    private String musicName;
    private String author;
    private String coverUrl;
    private String musicUrl;
    private String lyricUrl;

    // Getters
    public long getId() { return id; }
    public String getMusicName() { return musicName; }
    public String getAuthor() { return author; }
    public String getCoverUrl() { return coverUrl; }
    public String getMusicUrl() { return musicUrl; }
    public String getLyricUrl() { return lyricUrl; }
}
