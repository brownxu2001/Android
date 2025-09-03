/*
package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private AppDatabase db;
    private final List<GameEntity> cachedGames = new ArrayList<>(); // 修复: 移除了错误的final修饰符

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化数据库
        db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "game-database")
                .fallbackToDestructiveMigrationFrom(1)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = view.findViewById(R.id.search_edittext);
        recyclerView = view.findViewById(R.id.recycler_view);

        // 设置RecyclerView
        adapter = new GameAdapter(cachedGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // 加载上次搜索记录
        String lastSearch = requireContext()
                .getSharedPreferences("search_prefs", 0)
                .getString("last_search", "");
        searchEditText.setText(lastSearch);

        // 加载数据库中的数据
        new LoadGamesTask().execute();

        view.findViewById(R.id.search_button).setOnClickListener(v -> {
            String keyword = searchEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                saveSearchKeyword(keyword);
                performSearch(keyword);
            }
        });

        return view;
    }

    private void saveSearchKeyword(String keyword) {
        requireContext()
                .getSharedPreferences("search_prefs", 0)
                .edit()
                .putString("last_search", keyword)
                .apply();
    }

    private void performSearch(String keyword) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse<GameListResponse>> call = apiService.searchGames(keyword);

        call.enqueue(new Callback<ApiResponse<GameListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GameListResponse>> call,
                                   Response<ApiResponse<GameListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<GameListResponse> apiResponse = response.body();
                    if (apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                        List<Game> games = apiResponse.getData().getRecords();
                        saveToDatabase(games);
                        updateUI(games);
                    } else {
                        Toast.makeText(getContext(), "API错误: " +
                                        (apiResponse != null ? apiResponse.getMessage() : "Unknown"),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Empty response";
                        Log.e("API_ERROR", "错误响应: " + errorBody);
                    } catch (IOException e) {
                        Log.e("API_ERROR", "读取错误响应异常", e);
                    }
                    Toast.makeText(getContext(), "服务器响应异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GameListResponse>> call, Throwable t) {
                Log.e("NETWORK_ERROR", "网络请求失败", t);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToDatabase(List<Game> games) {
        List<GameEntity> entities = new ArrayList<>();
        for (Game game : games) {
            GameEntity entity = new GameEntity();
            entity.id = game.getId();
            entity.gameName = game.getGameName();
            entity.introduction = game.getIntroduction();
            entity.score = game.getScore();
            entity.playNumFormat = game.getPlayNumFormat();
            entities.add(entity);
        }

        // 修复: 正确传递参数到SaveGamesTask
        new SaveGamesTask(entities).execute();
    }

    private void updateUI(List<Game> games) {
        cachedGames.clear();
        for (Game game : games) {
            GameEntity entity = new GameEntity();
            entity.id = game.getId();
            entity.gameName = game.getGameName();
            entity.introduction = game.getIntroduction();
            entity.score = game.getScore();
            entity.playNumFormat = game.getPlayNumFormat();
            cachedGames.add(entity);
        }
        adapter.notifyDataSetChanged();
    }

    private class LoadGamesTask extends AsyncTask<Void, Void, List<GameEntity>> {
        @Override
        protected List<GameEntity> doInBackground(Void... voids) {
            try {
                return db.gameDao().getAll();
            } catch (Exception e) {
                Log.e("LoadGamesTask", "加载游戏数据失败", e);
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<GameEntity> result) {
            if (result != null) {
                cachedGames.clear();
                cachedGames.addAll(result);
                adapter.notifyDataSetChanged();
            }
        }
    }

    // 修复: 添加了正确的构造函数和games成员
    private class SaveGamesTask extends AsyncTask<Void, Void, Void> {
        private final List<GameEntity> games;

        SaveGamesTask(List<GameEntity> games) {
            this.games = games;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                db.runInTransaction(() -> {
                    db.gameDao().deleteAll();
                    db.gameDao().insert(games); // 修复: 使用成员变量
                    return null;
                });
            } catch (Exception e) {
                Log.e("SaveGamesTask", "保存游戏数据失败", e);
            }
            return null;
        }
    }
}*/
