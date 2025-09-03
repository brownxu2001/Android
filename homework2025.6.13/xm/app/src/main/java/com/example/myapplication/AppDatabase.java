package com.example.myapplication;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GameEntity.class}, version = 2) // 确保版本号为2
public abstract class AppDatabase extends RoomDatabase {
    public abstract GameDao gameDao();
}