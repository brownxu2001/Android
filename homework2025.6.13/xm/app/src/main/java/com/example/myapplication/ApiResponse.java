package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String message;

    @SerializedName("data")
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}