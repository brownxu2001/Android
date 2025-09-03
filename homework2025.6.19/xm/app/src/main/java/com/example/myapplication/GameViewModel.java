package com.example.myapplication;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class GameViewModel extends AndroidViewModel {

    private static final String BASE_URL = "https://hotfix-service-prod.g.mi.com/";
    private static final String PREFS_NAME = "SearchPrefs";
    private static final String LAST_SEARCH_KEY = "last_search";
    private static final String CACHED_DATA_KEY = "cached_data";

    private final MutableLiveData<List<GameItem>> gameItems = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> searchKeyword = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadMore = new MutableLiveData<>(false);

    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;
    private boolean hasMore = true;

    private final Gson gson = new Gson();
    private GameApiService gameApiService;

    public GameViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        loadCachedData();
    }

    private void initRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gameApiService = retrofit.create(GameApiService.class);
    }

    public LiveData<List<GameItem>> getGameItems() {
        return gameItems;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getSearchKeyword() {
        return searchKeyword;
    }

    public LiveData<Boolean> getIsLoadMore() {
        return isLoadMore;
    }

    public void resetPagination() {
        currentPage = 1;
        hasMore = true;
    }

    public void performSearch(String keyword) {
        performSearch(keyword, currentPage);
    }

    public void loadMore(String keyword) {
        if (hasMore && !isLoading.getValue()) {
            currentPage++;
            performSearch(keyword, currentPage);
        }
    }

    private void performSearch(String keyword, int page) {
        isLoading.setValue(true);
        if (page == 1) {
            isLoadMore.setValue(false);
        } else {
            isLoadMore.setValue(true);
        }

        Call<ApiResponse> call = gameApiService.searchGames(keyword, page, PAGE_SIZE);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading.setValue(false);
                isLoadMore.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.code == 200 && apiResponse.data != null) {
                        // 检查是否有更多数据
                        hasMore = apiResponse.data.records.size() >= PAGE_SIZE;

                        if (page == 1) {
                            saveSearchState(keyword, apiResponse);
                            updateGameList(apiResponse.data.records);
                        } else {
                            appendGameList(apiResponse.data.records);
                        }
                    } else {
                        errorMessage.setValue("API错误: " + apiResponse.msg);
                    }
                } else {
                    errorMessage.setValue("服务器响应异常: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
                isLoadMore.setValue(false);
                if (page > 1) currentPage--; // 回滚页码
                errorMessage.setValue("网络错误: " + t.getMessage());
            }
        });
    }

    private void updateGameList(List<Game> games) {
        List<GameItem> items = new ArrayList<>();
        for (Game game : games) {
            items.add(new GameItem(game));
        }
        gameItems.setValue(items);
    }

    private void appendGameList(List<Game> games) {
        List<GameItem> currentItems = gameItems.getValue() != null ?
                new ArrayList<>(gameItems.getValue()) : new ArrayList<>();

        for (Game game : games) {
            currentItems.add(new GameItem(game));
        }
        gameItems.setValue(currentItems);
    }

    private void loadCachedData() {
        SharedPreferences prefs = getApplication().getSharedPreferences(PREFS_NAME, 0);
        String lastSearch = prefs.getString(LAST_SEARCH_KEY, "");
        searchKeyword.setValue(lastSearch);

        String jsonData = prefs.getString(CACHED_DATA_KEY, null);
        if (jsonData != null) {
            try {
                ApiResponse response = gson.fromJson(jsonData, ApiResponse.class);
                if (response != null && response.data != null) {
                    updateGameList(response.data.records);
                }
            } catch (Exception e) {
                Log.e("LoadCache", "Failed to parse cached data", e);
                errorMessage.setValue("加载缓存数据失败");
            }
        }
    }

    private void saveSearchState(String keyword, ApiResponse response) {
        SharedPreferences prefs = getApplication().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LAST_SEARCH_KEY, keyword);

        if (response != null) {
            String jsonData = gson.toJson(response);
            editor.putString(CACHED_DATA_KEY, jsonData);
        }
        editor.apply();
    }

    public static class ApiResponse {
        int code;
        String msg;
        Data data;
    }

    public static class Data {
        List<Game> records;
        int total; // 确保API返回总记录数
    }

    public static class Game {
        int id;
        String gameName;
        String introduction;
        float score;
        String playNumFormat;
        String icon;
        String apkUrl;
    }

    public static class GameItem {
        int id;
        String name;
        String intro;
        float rating;
        String players;
        String iconUrl;

        public GameItem(Game game) {
            this.id = game.id;
            this.name = game.gameName;
            this.intro = game.introduction;
            this.rating = game.score;
            this.players = game.playNumFormat;
            this.iconUrl = game.icon;
        }
    }
    // 在 GameViewModel 中添加以下方法
    public boolean isLoadingMore() {
        return isLoadMore.getValue() != null && isLoadMore.getValue();
    }

    public boolean hasMoreData() {
        return hasMore;
    }
    public interface GameApiService {
        @GET("/quick-game/game/search")
        Call<ApiResponse> searchGames(
                @Query("search") String keyword,
                @Query("page") int page,
                @Query("pageSize") int pageSize
        );
    }
}