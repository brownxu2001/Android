package com.example.music_xuzhaocheng;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.example.music_xuzhaocheng.HomePageData;
import com.example.music_xuzhaocheng.HomePageInfo;
import com.example.music_xuzhaocheng.HomePageResponse;
import com.example.music_xuzhaocheng.MusicInfo;
import com.example.music_xuzhaocheng.ApiClient;
import com.example.music_xuzhaocheng.MusicApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager2 bannerViewPager;
    private CircleIndicator3 indicator;
    private NestedScrollView nestedScrollView;
    private TextView loadMoreView;
    private LinearLayout modulesContainer;

    private int currentPage = 1;
    private int pageSize = 4; // 默认每页4个模块
    private boolean isLoadingMore = false;
    private int totalPages = 1;

    // 模块标题映射
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
        bannerViewPager = findViewById(R.id.bannerViewPager);
        indicator = findViewById(R.id.indicator);
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
                            // 首次加载，清空现有视图
                            modulesContainer.removeAllViews();
                        }

                        // 处理数据并创建UI
                        processDataAndCreateUI(data.getRecords());

                        // 重置加载状态
                        isLoadingMore = false;
                    } else {
                        Toast.makeText(MainActivity.this, "数据加载失败: " + homeResponse.getMsg(), Toast.LENGTH_SHORT).show();
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
        for (HomePageInfo module : modules) {
            switch (module.getStyle()) {
                case 1: // Banner
                    createBannerModule(module);
                    break;
                case 2: // 横滑大卡
                    createHorizontalModule(module);
                    break;
                case 3: // 一行一列
                    createSingleColumnModule(module);
                    break;
                case 4: // 一行两列
                    createDoubleColumnModule(module);
                    break;
                default:
                    // 未知类型
                    break;
            }
        }
    }

    private void createBannerModule(HomePageInfo module) {
        // 如果banner存在，只使用第一个banner模块
        if (bannerViewPager.getAdapter() != null) return;

        // 创建Banner
        List<String> bannerUrls = new ArrayList<>();
        for (MusicInfo music : module.getMusicInfoList()) {
            bannerUrls.add(music.getCoverUrl());
        }

        BannerPagerAdapter adapter = new BannerPagerAdapter(bannerUrls);
        bannerViewPager.setAdapter(adapter);

        // 只有多图时设置指示器和自动轮播
        if (bannerUrls.size() > 1) {
            indicator.setViewPager(bannerViewPager);
            setupAutoScroll();
        } else {
            indicator.setVisibility(View.GONE);
        }
    }

    private void createHorizontalModule(HomePageInfo module) {
        // 创建标题
        String title = moduleTitleMap.getOrDefault(module.getModuleName(), module.getModuleName());
        createModuleTitle(title);

        // 创建横向RecyclerView
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(200) // 固定高度
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SongAdapter adapter = new SongAdapter(module.getMusicInfoList(), 2); // 2表示横滑样式
        recyclerView.setAdapter(adapter);

        modulesContainer.addView(recyclerView);
    }

    private void createSingleColumnModule(HomePageInfo module) {
        // 创建标题
        String title = moduleTitleMap.getOrDefault(module.getModuleName(), module.getModuleName());
        createModuleTitle(title);

        // 创建垂直RecyclerView
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SongAdapter adapter = new SongAdapter(module.getMusicInfoList(), 3); // 3表示一行一列样式
        recyclerView.setAdapter(adapter);

        modulesContainer.addView(recyclerView);
    }

    private void createDoubleColumnModule(HomePageInfo module) {
        // 创建标题
        String title = moduleTitleMap.getOrDefault(module.getModuleName(), module.getModuleName());
        createModuleTitle(title);

        // 创建网格RecyclerView
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        SongAdapter adapter = new SongAdapter(module.getMusicInfoList(), 4); // 4表示一行两列样式
        recyclerView.setAdapter(adapter);

        modulesContainer.addView(recyclerView);
    }

    private void createModuleTitle(String title) {
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        titleView.setTypeface(titleView.getTypeface(), Typeface.BOLD);
        titleView.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(8));
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
            currentPage = 1; // 重置到第一页
            fetchData(currentPage);
        });
    }

    private void setupLoadMore() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isScrollViewAtBottom(v) && !isLoadingMore && currentPage < totalPages) {
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
        int bottomDelta = (lastChild.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        return bottomDelta < dpToPx(100); // 当距离底部100dp时触发加载
    }

    private void setupAutoScroll() {
        final long AUTO_SCROLL_DELAY_MS = 5000; // 5秒

        if (bannerViewPager.getAdapter() == null || bannerViewPager.getAdapter().getItemCount() <= 1) {
            return;
        }

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (bannerViewPager.getAdapter() == null) return;

                int nextItem = bannerViewPager.getCurrentItem() + 1;
                if (nextItem >= bannerViewPager.getAdapter().getItemCount()) {
                    nextItem = 0;
                }
                bannerViewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, AUTO_SCROLL_DELAY_MS);
            }
        };

        handler.postDelayed(runnable, AUTO_SCROLL_DELAY_MS);

        // 停止自动轮播当用户交互时
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, AUTO_SCROLL_DELAY_MS);
            }
        });
    }
}