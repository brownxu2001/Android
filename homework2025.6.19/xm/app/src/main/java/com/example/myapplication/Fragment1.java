package com.example.myapplication;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class Fragment1 extends Fragment {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private GameAdapter adapter;
    private GameViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        searchEditText = view.findViewById(R.id.search_edittext);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progress_bar);

        // 初始化RecyclerView
        adapter = new GameAdapter(new ArrayList<>());
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // 观察游戏数据变化
        viewModel.getGameItems().observe(getViewLifecycleOwner(), gameItems -> {
            adapter.updateList(gameItems);
            if (gameItems.isEmpty()) {
                Toast.makeText(getContext(), "未找到相关游戏", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "找到 " + gameItems.size() + " 个结果", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                Toast.makeText(getContext(), "搜索中...", Toast.LENGTH_SHORT).show();
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel.getSearchKeyword().observe(getViewLifecycleOwner(), keyword -> {
            if (keyword != null && !keyword.isEmpty()) {
                searchEditText.setText(keyword);
            }
        });

        viewModel.getIsLoadMore().observe(getViewLifecycleOwner(), isLoadMore -> {
            if (isLoadMore != null) {
                progressBar.setVisibility(isLoadMore ? View.VISIBLE : View.GONE);
            }
        });

        view.findViewById(R.id.search_button).setOnClickListener(v -> {
            String keyword = searchEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                hideKeyboard();
                viewModel.resetPagination();
                viewModel.performSearch(keyword);
            } else {
                Toast.makeText(getContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            String keyword = searchEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                viewModel.resetPagination();
                viewModel.performSearch(keyword);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager =
                        (StaggeredGridLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    int[] lastVisibleItems = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisibleItem = Math.max(lastVisibleItems[0], lastVisibleItems[1]);
                    int totalItemCount = layoutManager.getItemCount();

                    if (lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                        String keyword = searchEditText.getText().toString().trim();
                        viewModel.loadMore(keyword);
                    }
                }
            }
        });

        return view;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) outRect.top = spacing;
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) outRect.top = spacing;
            }
        }
    }

    public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
        private List<GameViewModel.GameItem> items;

        public GameAdapter(List<GameViewModel.GameItem> items) {
            this.items = items;
        }

        public void updateList(List<GameViewModel.GameItem> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
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
            GameViewModel.GameItem item = items.get(position);

            holder.nameTextView.setText(item.name);
            holder.introTextView.setText(item.intro);
            holder.ratingTextView.setText(String.format("评分: %.1f", item.rating));
            holder.playersTextView.setText(String.format("玩家: %s", item.players));

            RequestOptions requestOptions = new RequestOptions()
                    .transform(new CenterCrop(), new RoundedCorners(16));

            Glide.with(holder.itemView.getContext())
                    .load(item.iconUrl)
                    .apply(requestOptions)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(holder.iconImageView);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iconImageView;
            TextView nameTextView;
            TextView introTextView;
            TextView ratingTextView;
            TextView playersTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                iconImageView = itemView.findViewById(R.id.icon_image_view);
                nameTextView = itemView.findViewById(R.id.name_text_view);
                introTextView = itemView.findViewById(R.id.intro_text_view);
                ratingTextView = itemView.findViewById(R.id.rating_text_view);
                playersTextView = itemView.findViewById(R.id.players_text_view);
            }
        }
    }
}
//
//package com.example.myapplication;
//
//import android.content.Context;
//import android.graphics.Rect;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.StaggeredGridLayoutManager;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.CenterCrop;
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
//import com.bumptech.glide.request.RequestOptions;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.module.LoadMoreModule;
//import com.chad.library.adapter.base.viewholder.BaseViewHolder;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Fragment1 extends Fragment {
//
//    private EditText searchEditText;
//    private RecyclerView recyclerView;
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private ProgressBar progressBar;
//    private GameAdapter adapter;
//    private GameViewModel viewModel;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_1, container, false);
//
//        // 初始化视图
//        searchEditText = view.findViewById(R.id.search_edittext);
//        recyclerView = view.findViewById(R.id.recycler_view);
//        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
//        progressBar = view.findViewById(R.id.progress_bar);
//
//        // 初始化适配器
//        adapter = new GameAdapter(new ArrayList<>());
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
//
//        // 添加列表项间距
//        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
//
//        // 初始化ViewModel
//        viewModel = new ViewModelProvider(this).get(GameViewModel.class);
//
//        // 观察游戏数据变化
//        viewModel.getGameItems().observe(getViewLifecycleOwner(), gameItems -> {
//            if (viewModel.isLoadingMore()) {
//                // 上拉加载更多
//                adapter.addData(gameItems);
//
//                // 如果没有更多数据，结束加载
//                if (!viewModel.hasMoreData()) {
//                    adapter.getLoadMoreModule().loadMoreEnd();
//                }
//            } else {
//                // 下拉刷新或首次加载
//                adapter.setNewInstance(gameItems);
//                adapter.getLoadMoreModule().loadMoreComplete();
//            }
//
//            if (gameItems.isEmpty()) {
//                Toast.makeText(getContext(), "未找到相关游戏", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // 观察错误消息
//        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
//            if (message != null && !message.isEmpty()) {
//                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//                if (viewModel.isLoadingMore()) {
//                    adapter.getLoadMoreModule().loadMoreFail();
//                }
//            }
//        });
//
//        // 观察加载状态
//        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
//            if (loading != null && loading) {
//                // 显示加载状态
//            } else {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
//
//        // 观察搜索关键词
//        viewModel.getSearchKeyword().observe(getViewLifecycleOwner(), keyword -> {
//            if (keyword != null && !keyword.isEmpty()) {
//                searchEditText.setText(keyword);
//            }
//        });
//
//        // 设置搜索按钮点击事件
//        view.findViewById(R.id.search_button).setOnClickListener(v -> {
//            String keyword = searchEditText.getText().toString().trim();
//            if (!keyword.isEmpty()) {
//                hideKeyboard();
//                viewModel.resetPagination();
//                viewModel.performSearch(keyword);
//            } else {
//                Toast.makeText(getContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // 设置下拉刷新
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            String keyword = searchEditText.getText().toString().trim();
//            if (!keyword.isEmpty()) {
//                viewModel.resetPagination();
//                viewModel.performSearch(keyword);
//            } else {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
//
//        // 设置上拉加载监听
//        adapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
//            String keyword = searchEditText.getText().toString().trim();
//            if (!keyword.isEmpty()) {
//                viewModel.loadMore(keyword);
//            }
//        });
//
//        return view;
//    }
//
//    // 隐藏键盘
//    private void hideKeyboard() {
//        InputMethodManager imm = (InputMethodManager) requireContext()
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
//        }
//    }
//
//    // 列表项间距装饰器
//    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//        private final int spanCount;
//        private final int spacing;
//        private final boolean includeEdge;
//
//        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
//            this.spanCount = spanCount;
//            this.spacing = spacing;
//            this.includeEdge = includeEdge;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            int position = parent.getChildAdapterPosition(view);
//            int column = position % spanCount;
//
//            if (includeEdge) {
//                outRect.left = spacing - column * spacing / spanCount;
//                outRect.right = (column + 1) * spacing / spanCount;
//                if (position < spanCount) outRect.top = spacing;
//                outRect.bottom = spacing;
//            } else {
//                outRect.left = column * spacing / spanCount;
//                outRect.right = spacing - (column + 1) * spacing / spanCount;
//                if (position >= spanCount) outRect.top = spacing;
//            }
//        }
//    }
//
//    // 游戏列表适配器 - 解决所有编译错误
//    public static class GameAdapter extends BaseQuickAdapter<GameViewModel.GameItem, BaseViewHolder>
//            implements LoadMoreModule {
//
//        public GameAdapter(List<GameViewModel.GameItem> items) {
//            super(R.layout.item_game, items);
//            // 开启动画
//            setAnimationEnable(true);
//            setAnimationWithDefault(AnimationType.ScaleIn);
//        }
//
//        @Override
//        protected void convert(@NonNull BaseViewHolder holder, GameViewModel.GameItem item) {
//            // 获取视图组件 (修复所有符号解析问题)
//            TextView nameTextView = holder.getView(R.id.name_text_view);
//            TextView introTextView = holder.getView(R.id.intro_text_view);
//            TextView ratingTextView = holder.getView(R.id.rating_text_view);
//            TextView playersTextView = holder.getView(R.id.players_text_view);
//            ImageView iconImageView = holder.getView(R.id.icon_image_view);
//
//            // 设置文本内容
//            nameTextView.setText(item.name);
//            introTextView.setText(item.intro);
//            ratingTextView.setText(String.format("评分: %.1f", item.rating));
//            playersTextView.setText(String.format("玩家: %s", item.players));
//
//            // 使用 holder.itemView 获取上下文 (修复453行错误)
//            RequestOptions requestOptions = new RequestOptions()
//                    .transform(new CenterCrop(), new RoundedCorners(16));
//
//            Glide.with(holder.itemView.getContext())  // 修复 itemView 访问方式
//                    .load(item.iconUrl)
//                    .apply(requestOptions)
//                    .placeholder(R.drawable.placeholder)
//                    .error(R.drawable.error)
//                    .into(iconImageView);
//        }
//
//
//        @Override
//        public void addLoadMoreModule(BaseQuickAdapter<?, ?> adapter) {
//
//        }
//    }
//}