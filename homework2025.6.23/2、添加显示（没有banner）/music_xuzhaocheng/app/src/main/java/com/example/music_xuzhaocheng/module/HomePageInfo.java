// HomePageInfo.java
package com.example.music_xuzhaocheng.module;

import java.util.List;

public class HomePageInfo {
    private int moduleConfigId; // 模块配置ID
    private String moduleName; // 模块名称
    private int style; // 样式 (1:banner, 2:横滑大卡, 3:一行一列, 4:一行两列)
    private List<MusicInfo> musicInfoList; // 模块下音乐列表信息

    // getter方法
    public int getModuleConfigId() { return moduleConfigId; }
    public String getModuleName() { return moduleName; }
    public int getStyle() { return style; }
    public List<MusicInfo> getMusicInfoList() { return musicInfoList; }
}