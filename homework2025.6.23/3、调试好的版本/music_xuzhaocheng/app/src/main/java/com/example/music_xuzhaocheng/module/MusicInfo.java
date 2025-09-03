// MusicInfo.java
package com.example.music_xuzhaocheng.module;

public class MusicInfo {
    private long id; // 音乐ID
    private String musicName; // 音乐名称
    private String author; // 作者/歌手
    private String coverUrl; // 封面图
    private String musicUrl;
    private String lyricUrl; // 音乐歌词文件地址

    // getter方法
    public long getId() { return id; }
    public String getMusicName() { return musicName; }
    public String getAuthor() { return author; }
    public String getCoverUrl() { return coverUrl; }
    public String getMusicUrl() { return musicUrl; }
    public String getLyricUrl() { return lyricUrl; }

    // 添加setter方法
    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }
}