package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "games")
public class GameEntity {
    @PrimaryKey
    public int id;
    public String gameName;
    public String introduction;
    public float score; // 必须存在的字段
    public String playNumFormat; // 必须存在的字段
}