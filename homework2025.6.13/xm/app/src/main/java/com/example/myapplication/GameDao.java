package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDao {
    @Query("SELECT * FROM games")
    List<GameEntity> getAll();

    @Insert
    void insert(List<GameEntity> games);

    @Query("DELETE FROM games")
    void deleteAll();
}