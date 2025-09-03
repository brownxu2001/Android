package com.example.myapplication;

// ApiResponse.java
public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;

    public void setCode(int code) { this.code = code; }
    public void setMsg(String msg) { this.msg = msg; }
    public void setData(T data) { this.data = data; }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public T getData() { return data; }
}