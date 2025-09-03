package com.example.music_xuzhaocheng;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MusicApiService {
    @GET("music/homePage")
    @Headers("Content-Type: application/json")
    Call<HomePageResponse> getHomePageData(
            @Query("current") int page,
            @Query("size") int pageSize
    );
}