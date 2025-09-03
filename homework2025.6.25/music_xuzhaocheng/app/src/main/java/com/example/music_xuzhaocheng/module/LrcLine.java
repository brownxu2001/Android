package com.example.music_xuzhaocheng.module;

public class LrcLine {
    private long time; // 毫秒时间戳
    private String text;

    public LrcLine(long time, String text) {
        this.time = time;
        this.text = text;
    }

    public long getTime() { return time; }
    public String getText() { return text; }
}

