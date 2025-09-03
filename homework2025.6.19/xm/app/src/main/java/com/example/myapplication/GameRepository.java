package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// GameRepository.java
public class GameRepository {
    private static final String BASE_URL = "https://hotfix-service-prod.g.mi.com/quick-game/game/";

    public LiveData<ApiResponse<GameInfo>> getGameInfo(String gameId) {
        MutableLiveData<ApiResponse<GameInfo>> liveData = new MutableLiveData<>();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + gameId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ApiResponse<GameInfo> response = new ApiResponse<>();
                response.setCode(500);
                response.setMsg("Network error: " + e.getMessage());
                liveData.postValue(response);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    ApiResponse<GameInfo> apiResponse = gson.fromJson(json,
                            new TypeToken<ApiResponse<GameInfo>>(){}.getType());
                    liveData.postValue(apiResponse);
                } else {
                    ApiResponse<GameInfo> errorResponse = new ApiResponse<>();
                    errorResponse.setCode(response.code());
                    errorResponse.setMsg("Request failed");
                    liveData.postValue(errorResponse);
                }
            }
        });

        return liveData;
    }
}
