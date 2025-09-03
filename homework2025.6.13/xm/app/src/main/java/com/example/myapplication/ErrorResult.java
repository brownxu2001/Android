package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class ErrorResult {
    @SerializedName("error")
    private String errorMessage;

    @SerializedName("code")
    private int errorCode;

    public String getMessage() {
        return "[" + errorCode + "] " + errorMessage;
    }
}