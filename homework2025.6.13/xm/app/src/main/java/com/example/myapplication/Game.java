package com.example.myapplication;


import com.google.gson.annotations.SerializedName;

public class Game {
    @SerializedName("id")
    private int id;

    @SerializedName("gameName")
    private String gameName;

    @SerializedName("packageName")
    private String packageName;

    @SerializedName("introduction")  // 确保注解正确
    private String introduction;    // 字段名必须匹配 JSON

    @SerializedName("brief")
    private String brief;

    @SerializedName("score")
    private float score;

    @SerializedName("playNumFormat")
    private String playNumFormat;

    // Getter 方法（特别是 getIntroduction）
    public int getId() { return id; }
    public String getGameName() { return gameName; }
    public String getPackageName() { return packageName; }
    public String getIntroduction() { return introduction; }  // 关键添加
    public String getBrief() { return brief; }
    public float getScore() { return score; }
    public String getPlayNumFormat() { return playNumFormat; }
}