package com.example.music_xuzhaocheng.module;

import java.util.List;

public class HomePageInfo {
    private int moduleConfigId;
    private String moduleName;
    private int style;
    private List<MusicInfo> musicInfoList;

    public int getModuleConfigId() { return moduleConfigId; }
    public String getModuleName() { return moduleName; }
    public int getStyle() { return style; }
    public List<MusicInfo> getMusicInfoList() { return musicInfoList; }
}