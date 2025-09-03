package com.example.myapplication;
import android.provider.ContactsContract;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiService {
    @GET("quick-game/game/search")
    Call<ApiResponse<GameListResponse>> searchGames(
            @Query("search") String keyword
    );
}