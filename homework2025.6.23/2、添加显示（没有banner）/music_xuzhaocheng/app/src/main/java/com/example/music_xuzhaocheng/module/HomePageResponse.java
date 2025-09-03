// HomePageResponse.java
package com.example.music_xuzhaocheng.module;

public class HomePageResponse {
    private int code; // 业务返回码
    private String msg; // 返回消息
    private HomePageData data; // 首页信息

    // getter方法
    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public HomePageData getData() { return data; }
}