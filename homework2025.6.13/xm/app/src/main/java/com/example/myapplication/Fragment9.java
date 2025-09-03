package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import android.util.Log;

public class Fragment9 extends Fragment {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private List<GameItem> cachedGames = new ArrayList<>();
    private static final String BASE_URL = "https://hotfix-service-prod.g.mi.com/";
    private static final String PREFS_NAME = "SearchPrefs";
    private static final String LAST_SEARCH_KEY = "last_search";
    private static final String CACHED_DATA_KEY = "cached_data";
    private final Gson gson = new Gson();

    public static class ApiResponse {
        int code;
        String msg;
        Data data;
    }

    public static class Data {
        List<Game> records;
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
    public interface GameApiService {
        @GET("/quick-game/game/search")
        Call<ApiResponse> searchGames(@Query("search") String keyword);
    }

    public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
        private final List<GameItem> items;

        public GameAdapter(List<GameItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_game, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GameItem item = items.get(position);
            holder.nameTextView.setText(item.name);
            holder.introTextView.setText(item.intro);
        }


        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;
            TextView introTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.name_text_view);
                introTextView = itemView.findViewById(R.id.intro_text_view);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        searchEditText = view.findViewById(R.id.search_edittext);
        recyclerView = view.findViewById(R.id.recycler_view);


        adapter = new GameAdapter(cachedGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        loadCachedData();


        view.findViewById(R.id.search_button).setOnClickListener(v -> {
            String keyword = searchEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {

                hideKeyboard();
                performSearch(keyword);
            } else {
                Toast.makeText(getContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    private void loadCachedData() {

        String lastSearch = getSharedPrefs().getString(LAST_SEARCH_KEY, "");
        searchEditText.setText(lastSearch);


        String jsonData = getSharedPrefs().getString(CACHED_DATA_KEY, null);
        if (jsonData != null) {
            try {
                ApiResponse response = gson.fromJson(jsonData, ApiResponse.class);
                if (response != null && response.data != null) {
                    updateGameList(response.data.records);
                }
            } catch (Exception e) {
                Log.e("LoadCache", "Failed to parse cached data", e);
            }
        }
    }


    private void saveSearchState(String keyword, ApiResponse response) {

        getSharedPrefs().edit().putString(LAST_SEARCH_KEY, keyword).apply();


        if (response != null) {
            String jsonData = gson.toJson(response);
            getSharedPrefs().edit().putString(CACHED_DATA_KEY, jsonData).apply();
        }
    }

    private android.content.SharedPreferences getSharedPrefs() {
        return requireContext().getSharedPreferences(PREFS_NAME, 0);
    }


    private void performSearch(String keyword) {

        Toast.makeText(getContext(), "搜索中: " + keyword, Toast.LENGTH_SHORT).show();


        GameApiService service = createRetrofitService();
        Call<ApiResponse> call = service.searchGames(keyword);

        Log.d("API_REQUEST", "请求URL: " + call.request().url());

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.code == 200 && apiResponse.data != null) {
                        // 保存搜索结果
                        saveSearchState(keyword, apiResponse);
                        // 更新UI
                        updateGameList(apiResponse.data.records);
                    } else {
                        showError("API错误: " + apiResponse.msg);
                    }
                } else {
                    showError("服务器响应异常: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showError("网络错误: " + t.getMessage());
            }
        });
    }


    private void updateGameList(List<Game> games) {
        cachedGames.clear();
        for (Game game : games) {
            cachedGames.add(new GameItem(game));
        }
        adapter.notifyDataSetChanged();


        if (cachedGames.isEmpty()) {
            Toast.makeText(getContext(), "未找到相关游戏", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "找到 " + cachedGames.size() + " 个结果", Toast.LENGTH_SHORT).show();
        }
    }


    private GameApiService createRetrofitService() {

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

        return retrofit.create(GameApiService.class);
    }


    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.e("API_ERROR", message);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }
}