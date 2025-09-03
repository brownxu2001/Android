package com.example.music_xuzhaocheng.network;

import com.example.music_xuzhaocheng.module.HomePageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusicApiService {
    @GET("music/homePage")
    Call<HomePageResponse> getHomePageData(
            @Query("current") int current,  // 当前页，默认1
            @Query("size") int size          // 当前页大小
    );
}