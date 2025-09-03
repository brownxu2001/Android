package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GameListResponse {
    @SerializedName("records")
    private List<Game> records;

    @SerializedName("total")
    private int total;

    @SerializedName("size")
    private int size;

    @SerializedName("current")
    private int current;

    @SerializedName("pages")
    private int pages;

    public List<Game> getRecords() {
        return records;
    }
}