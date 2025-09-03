//package com.example.music_xuzhaocheng;
//
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.widget.NestedScrollView;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.bumptech.glide.Glide;
//import com.example.music_xuzhaocheng.Adapter.BannerPagerAdapter;
//import com.example.music_xuzhaocheng.Adapter.SongAdapter;
//import com.example.music_xuzhaocheng.module.HomePageData;
//import com.example.music_xuzhaocheng.module.HomePageInfo;
//import com.example.music_xuzhaocheng.module.HomePageResponse;
//import com.example.music_xuzhaocheng.module.MusicInfo;
//import com.example.music_xuzhaocheng.network.ApiClient;
//import com.example.music_xuzhaocheng.network.MusicApiService;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import me.relex.circleindicator.CircleIndicator3;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final String TAG = "MainActivity";
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private ViewPager2 bannerViewPager;
//    private CircleIndicator3 indicator;
//    private NestedScrollView nestedScrollView;
//    private TextView loadMoreView;
//    private LinearLayout modulesContainer;
//
//    // 用于保存各个模块的视图
//    private View bannerModuleView;
//    private View horizontalModuleView;
//    private View singleColumnModuleView;
//    private RecyclerView doubleColumnRecyclerView;
//    private SongAdapter doubleColumnAdapter;
//
//    private int currentPage = 1;
//    private int pageSize = 5;
//    private boolean isLoadingMore = false;
//    private int totalPages = 1;
//
//    private final Handler autoScrollHandler = new Handler();
//    private Runnable autoScrollRunnable;
//
//    // 模块标题映射
//    private final Map<String, String> moduleTitleMap = new HashMap<String, String>() {{
//        put("banner", "热门推荐");
//        put("横滑大卡", "独家精选");
//        put("一行一列", "每日推荐");
//        put("一行两列", "热门榜单");
//    }};
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initViews();
//        setupRefresh();
//        setupLoadMore();
//        fetchData(currentPage);
//    }
//
//    private void initViews() {
//        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
//        bannerViewPager = findViewById(R.id.bannerViewPager);
//        indicator = findViewById(R.id.indicator);
//        nestedScrollView = findViewById(R.id.nestedScrollView);
//        loadMoreView = findViewById(R.id.loadMoreView);
//        modulesContainer = findViewById(R.id.modulesContainer);
//    }
//
//    private void fetchData(final int page) {
//        MusicApiService apiService = ApiClient.getClient().create(MusicApiService.class);
//        Call<HomePageResponse> call = apiService.getHomePageData(page, pageSize);
//
//        call.enqueue(new Callback<HomePageResponse>() {
//            @Override
//            public void onResponse(Call<HomePageResponse> call, Response<HomePageResponse> response) {
//                swipeRefreshLayout.setRefreshing(false);
//                loadMoreView.setVisibility(View.GONE);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    HomePageResponse homeResponse = response.body();
//                    if (homeResponse.getCode() == 200) {
//                        HomePageData data = homeResponse.getData();
//                        totalPages = data.getPages();
//
//                        // 如果是下拉刷新（第一页），重新创建所有模块
//                        if (page == 1) {
//                            modulesContainer.removeAllViews();
//                            bannerModuleView = null;
//                            horizontalModuleView = null;
//                            singleColumnModuleView = null;
//                            doubleColumnRecyclerView = null;
//                            doubleColumnAdapter = null;
//                        }
//
//                        processDataAndCreateUI(data.getRecords());
//
//                        isLoadingMore = false;
//                    } else {
//                        Toast.makeText(MainActivity.this,
//                                "数据加载失败: " + homeResponse.getMsg(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "服务器响应错误", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<HomePageResponse> call, Throwable t) {
//                swipeRefreshLayout.setRefreshing(false);
//                loadMoreView.setVisibility(View.GONE);
//                Toast.makeText(MainActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                isLoadingMore = false;
//            }
//        });
//    }
//
//    private void processDataAndCreateUI(List<HomePageInfo> modules) {
//        if (modules == null || modules.isEmpty()) {
//            Toast.makeText(this, "没有可显示的内容", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        for (HomePageInfo module : modules) {
//            switch (module.getStyle()) {
//                case 1: // Banner样式
//                    if (bannerModuleView == null) {
//                        createBannerModule(module);
//                    }
//                    break;
//                case 2: // 横滑大卡
//                    if (horizontalModuleView == null) {
//                        createHorizontalModule(module);
//                    }
//                    break;
//                case 3: // 一行一列
//                    if (singleColumnModuleView == null) {
//                        createSingleColumnModule(module);
//                    }
//                    break;
//                case 4: // 一行两列
//                    if (doubleColumnRecyclerView == null) {
//                        createDoubleColumnModule(module);
//                    } else {
//                        // 上拉加载时只更新一行两列模块
//                        updateDoubleColumnModule(module);
//                    }
//                    break;
//                default:
//                    Log.w(TAG, "未知模块类型: " + module.getStyle());
//            }
//        }
//    }
//
//    private void createBannerModule(HomePageInfo module) {
//        List<String> bannerUrls = new ArrayList<>();
//        for (MusicInfo music : module.getMusicInfoList()) {
//            if (music.getCoverUrl() != null) {
//                bannerUrls.add(music.getCoverUrl());
//            }
//        }
//
//        if (bannerUrls.isEmpty()) {
//            Log.w(TAG, "Banner模块没有有效图片URL");
//            return;
//        }
//
//        BannerPagerAdapter adapter = new BannerPagerAdapter(bannerUrls);
//        bannerViewPager.setAdapter(adapter);
//
//        if (bannerUrls.size() > 1) {
//            indicator.setViewPager(bannerViewPager);
//            setupAutoScroll();
//        } else {
//            indicator.setVisibility(View.GONE);
//        }
//
//        bannerModuleView = bannerViewPager;
//    }
//
//    private void createHorizontalModule(HomePageInfo module) {
//        if (module.getMusicInfoList() == null || module.getMusicInfoList().isEmpty()) {
//            Log.w(TAG, "横滑大卡模块没有数据");
//            return;
//        }
//
//        // 添加模块标题
//        createModuleTitle(module.getModuleName());
//
//        RecyclerView recyclerView = new RecyclerView(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                dpToPx(200)
//        );
//        layoutParams.topMargin = dpToPx(8);
//        layoutParams.bottomMargin = dpToPx(16);
//        recyclerView.setLayoutParams(layoutParams);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(
//                this,
//                LinearLayoutManager.HORIZONTAL,
//                false
//        ));
//
//        SongAdapter adapter = new SongAdapter(
//                module.getMusicInfoList(),
//                2,  // 横滑大卡样式
//                this
//        );
//        recyclerView.setAdapter(adapter);
//
//        modulesContainer.addView(recyclerView);
//        horizontalModuleView = recyclerView;
//    }
//
//    private void createSingleColumnModule(HomePageInfo module) {
//        if (module.getMusicInfoList() == null || module.getMusicInfoList().isEmpty()) {
//            Log.w(TAG, "一行一列模块没有数据");
//            return;
//        }
//
//        // 添加模块标题
//        createModuleTitle(module.getModuleName());
//
//        RecyclerView recyclerView = new RecyclerView(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        layoutParams.topMargin = dpToPx(8);
//        layoutParams.bottomMargin = dpToPx(16);
//        recyclerView.setLayoutParams(layoutParams);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        SongAdapter adapter = new SongAdapter(
//                module.getMusicInfoList(),
//                3,  // 一行一列样式
//                this
//        );
//        recyclerView.setAdapter(adapter);
//
//        modulesContainer.addView(recyclerView);
//        singleColumnModuleView = recyclerView;
//    }
//
//    private void createDoubleColumnModule(HomePageInfo module) {
//        if (module.getMusicInfoList() == null || module.getMusicInfoList().isEmpty()) {
//            Log.w(TAG, "一行两列模块没有数据");
//            return;
//        }
//
//        // 添加模块标题
//        createModuleTitle(module.getModuleName());
//
//        RecyclerView recyclerView = new RecyclerView(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        layoutParams.topMargin = dpToPx(8);
//        layoutParams.bottomMargin = dpToPx(16);
//        recyclerView.setLayoutParams(layoutParams);
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
//        recyclerView.setLayoutManager(gridLayoutManager);
//
//        doubleColumnAdapter = new SongAdapter(
//                module.getMusicInfoList(),
//                4,  // 一行两列样式
//                this
//        );
//        recyclerView.setAdapter(doubleColumnAdapter);
//
//        modulesContainer.addView(recyclerView);
//        doubleColumnRecyclerView = recyclerView;
//    }
//
//    private void updateDoubleColumnModule(HomePageInfo module) {
//        if (module.getMusicInfoList() == null || module.getMusicInfoList().isEmpty()) {
//            Log.w(TAG, "一行两列模块没有新数据");
//            return;
//        }
//
//        if (doubleColumnAdapter != null) {
//            // 只添加新数据，不创建新视图
//            doubleColumnAdapter.addMoreData(module.getMusicInfoList());
//        }
//    }
//
//    private void createModuleTitle(String title) {
//        String displayTitle = moduleTitleMap.getOrDefault(title, title);
//        TextView titleView = new TextView(this);
//        titleView.setText(displayTitle);
//        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//        titleView.setTypeface(null, Typeface.BOLD);
//        titleView.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(8));
//        titleView.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        ));
//        modulesContainer.addView(titleView);
//    }
//
//    private int dpToPx(int dp) {
//        return (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                dp,
//                getResources().getDisplayMetrics()
//        );
//    }
//
//    private void setupRefresh() {
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            currentPage = 1; // 重置到第一页
//            fetchData(currentPage);
//        });
//    }
//
//    private void setupLoadMore() {
//        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (isScrollViewAtBottom(v) && !isLoadingMore && currentPage < totalPages) {
//                isLoadingMore = true;
//                loadMoreView.setVisibility(View.VISIBLE);
//                currentPage++;
//                fetchData(currentPage);
//            }
//        });
//    }
//
//    private boolean isScrollViewAtBottom(View view) {
//        if (!(view instanceof NestedScrollView)) return false;
//        NestedScrollView scrollView = (NestedScrollView) view;
//        View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
//        int bottomDelta = (lastChild.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
//        return bottomDelta < dpToPx(100);
//    }
//
//    private void setupAutoScroll() {
//        final long AUTO_SCROLL_DELAY_MS = 5000;
//
//        final Handler handler = new Handler();
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                int nextItem = bannerViewPager.getCurrentItem() + 1;
//                bannerViewPager.setCurrentItem(nextItem, true);
//                handler.postDelayed(this, AUTO_SCROLL_DELAY_MS);
//            }
//        };
//
//        handler.postDelayed(runnable, AUTO_SCROLL_DELAY_MS);
//
//        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                handler.removeCallbacks(runnable);
//                handler.postDelayed(runnable, AUTO_SCROLL_DELAY_MS);
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (autoScrollHandler != null && autoScrollRunnable != null) {
//            autoScrollHandler.removeCallbacks(autoScrollRunnable);
//        }
//    }
//}



package com.example.music_xuzhaocheng;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.music_xuzhaocheng.Adapter.BannerPagerAdapter;
import com.example.music_xuzhaocheng.Adapter.SongAdapter;
import com.example.music_xuzhaocheng.module.HomePageData;
import com.example.music_xuzhaocheng.module.HomePageInfo;
import com.example.music_xuzhaocheng.module.HomePageResponse;
import com.example.music_xuzhaocheng.module.MusicInfo;
import com.example.music_xuzhaocheng.network.ApiClient;
import com.example.music_xuzhaocheng.network.MusicApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView nestedScrollView;
    private TextView loadMoreView;
    private LinearLayout modulesContainer;

    private View bannerModuleView;
    private View horizontalModuleView;
    private View singleColumnModuleView;
    private RecyclerView doubleColumnRecyclerView;
    private SongAdapter doubleColumnAdapter;

    private int currentPage = 1;
    private int pageSize = 5;
    private boolean isLoadingMore = false;
    private int totalPages = 1;

    private final Map<String, String> moduleTitleMap = new HashMap<String, String>() {{
        put("banner", "热门推荐");
        put("横滑大卡", "独家精选");
        put("一行一列", "每日推荐");
        put("一行两列", "热门榜单");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRefresh();
        setupLoadMore();
        fetchData(currentPage);
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        loadMoreView = findViewById(R.id.loadMoreView);
        modulesContainer = findViewById(R.id.modulesContainer);
    }

    private void fetchData(final int page) {
        MusicApiService apiService = ApiClient.getClient().create(MusicApiService.class);
        Call<HomePageResponse> call = apiService.getHomePageData(page, pageSize);

        call.enqueue(new Callback<HomePageResponse>() {
            @Override
            public void onResponse(Call<HomePageResponse> call, Response<HomePageResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                loadMoreView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    HomePageResponse homeResponse = response.body();
                    if (homeResponse.getCode() == 200) {
                        HomePageData data = homeResponse.getData();
                        totalPages = data.getPages();

                        if (page == 1) {
                            modulesContainer.removeAllViews();
                            bannerModuleView = null;
                            horizontalModuleView = null;
                            singleColumnModuleView = null;
                            doubleColumnRecyclerView = null;
                            doubleColumnAdapter = null;
                        }

                        processDataAndCreateUI(data.getRecords());
                        isLoadingMore = false;
                    } else {
                        Toast.makeText(MainActivity.this,
                                "数据加载失败: " + homeResponse.getMsg(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "服务器响应错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HomePageResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                loadMoreView.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isLoadingMore = false;
            }
        });
    }

    private void processDataAndCreateUI(List<HomePageInfo> modules) {
        if (modules == null || modules.isEmpty()) {
            Toast.makeText(this, "没有可显示的内容", Toast.LENGTH_SHORT).show();
            return;
        }

        for (HomePageInfo module : modules) {
            switch (module.getStyle()) {
                case 1:
                    if (bannerModuleView == null) {
                        createBannerModule(module);
                    }
                    break;
                case 2:
                    if (horizontalModuleView == null) {
                        createHorizontalModule(module);
                    }
                    break;
                case 3:
                    if (singleColumnModuleView == null) {
                        createSingleColumnModule(module);
                    }
                    break;
                case 4:
                    if (doubleColumnRecyclerView == null) {
                        createDoubleColumnModule(module);
                    } else {
                        updateDoubleColumnModule(module);
                    }
                    break;
                default:
                    Log.w(TAG, "未知模块类型: " + module.getStyle());
            }
        }
    }

    private void createBannerModule(HomePageInfo module) {
        List<String> bannerUrls = new ArrayList<>();
        for (MusicInfo music : module.getMusicInfoList()) {
            if (music.getCoverUrl() != null && !music.getCoverUrl().trim().isEmpty()) {
                bannerUrls.add(music.getCoverUrl());
            }
        }

        if (bannerUrls.isEmpty()) {
            Log.w(TAG, "Banner模块没有有效图片URL");
            return;
        }

        LinearLayout bannerContainer = new LinearLayout(this);
        bannerContainer.setOrientation(LinearLayout.VERTICAL);
        bannerContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ViewPager2 viewPager = new ViewPager2(this);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(220)
        ));
        BannerPagerAdapter adapter = new BannerPagerAdapter(bannerUrls);
        viewPager.setAdapter(adapter);

        CircleIndicator3 indicator = new CircleIndicator3(this);
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(24)
        );
        indicatorParams.topMargin = -dpToPx(24);
        indicator.setLayoutParams(indicatorParams);
        indicator.setViewPager(viewPager);

        bannerContainer.addView(viewPager);
        bannerContainer.addView(indicator);
        modulesContainer.addView(bannerContainer);
        bannerModuleView = bannerContainer;

        if (bannerUrls.size() > 1) {
            setupAutoScroll(viewPager);
        } else {
            indicator.setVisibility(View.GONE);
        }
    }

    private void setupAutoScroll(final ViewPager2 viewPager) {
        final long AUTO_SCROLL_DELAY_MS = 5000;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int itemCount = viewPager.getAdapter() != null ? viewPager.getAdapter().getItemCount() : 0;
                if (itemCount > 1) {
                    int nextItem = (viewPager.getCurrentItem() + 1) % itemCount;
                    viewPager.setCurrentItem(nextItem, true);
                    handler.postDelayed(this, AUTO_SCROLL_DELAY_MS);
                }
            }
        };

        handler.postDelayed(runnable, AUTO_SCROLL_DELAY_MS);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, AUTO_SCROLL_DELAY_MS);
            }
        });
    }

    private void createHorizontalModule(HomePageInfo module) {
        if (module.getMusicInfoList() == null || module.getMusicInfoList().isEmpty()) return;

        createModuleTitle(module.getModuleName());

        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(200)
        );
        layoutParams.topMargin = dpToPx(8);
        layoutParams.bottomMargin = dpToPx(16);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SongAdapter adapter = new SongAdapter(module.getMusicInfoList(), 2, this);
        recyclerView.setAdapter(adapter);

        modulesContainer.addView(recyclerView);
        horizontalModuleView = recyclerView;
    }

    private void createSingleColumnModule(HomePageInfo module) {
        if (module.getMusicInfoList() == null || module.getMusicInfoList().isEmpty()) return;

        createModuleTitle(module.getModuleName());

        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = dpToPx(8);
        layoutParams.bottomMargin = dpToPx(16);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SongAdapter adapter = new SongAdapter(module.getMusicInfoList(), 3, this);
        recyclerView.setAdapter(adapter);

        modulesContainer.addView(recyclerView);
        singleColumnModuleView = recyclerView;
    }

    private void createDoubleColumnModule(HomePageInfo module) {
        if (module.getMusicInfoList() == null || module.getMusicInfoList().isEmpty()) return;

        createModuleTitle(module.getModuleName());

        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = dpToPx(8);
        layoutParams.bottomMargin = dpToPx(16);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        doubleColumnAdapter = new SongAdapter(module.getMusicInfoList(), 4, this);
        recyclerView.setAdapter(doubleColumnAdapter);

        modulesContainer.addView(recyclerView);
        doubleColumnRecyclerView = recyclerView;
    }

    private void updateDoubleColumnModule(HomePageInfo module) {
        if (module.getMusicInfoList() != null && doubleColumnAdapter != null) {
            doubleColumnAdapter.addMoreData(module.getMusicInfoList());
        }
    }

    private void createModuleTitle(String title) {
        String displayTitle = moduleTitleMap.getOrDefault(title, title);
        TextView titleView = new TextView(this);
        titleView.setText(displayTitle);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(8));
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        modulesContainer.addView(titleView);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void setupRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            fetchData(currentPage);
        });
    }

    private void setupLoadMore() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isScrollViewAtBottom(v)) {
                    isLoadingMore = true;
                    loadMoreView.setVisibility(View.VISIBLE);
                    currentPage++;
                    fetchData(currentPage);
                }
            }
        });

    }

    private boolean isScrollViewAtBottom(View view) {
        if (!(view instanceof NestedScrollView)) return false;
        NestedScrollView scrollView = (NestedScrollView) view;
        View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int bottomDelta = lastChild.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
        return bottomDelta < dpToPx(100);
    }
}
