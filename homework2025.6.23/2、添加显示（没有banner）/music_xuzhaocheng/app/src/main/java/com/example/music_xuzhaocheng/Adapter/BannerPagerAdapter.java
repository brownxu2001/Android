package com.example.music_xuzhaocheng.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_xuzhaocheng.R;

import java.util.List;

public class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.BannerViewHolder> {

    private final List<String> bannerUrls;
    private final boolean isMultiImage;
    // 添加最大页面数量限制（解决ANR问题）
    private static final int MAX_PAGES = 100;

    public BannerPagerAdapter(List<String> bannerUrls) {
        this.bannerUrls = bannerUrls;
        this.isMultiImage = bannerUrls != null && bannerUrls.size() > 1;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 修正：使用正确的小写变量名itemView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        if (bannerUrls == null || bannerUrls.isEmpty()) {
            loadImage(holder.bannerImage, R.drawable.banner_placeholder);
            return;
        }

        int actualPosition = isMultiImage ? position % bannerUrls.size() : position;
        loadImage(holder.bannerImage, bannerUrls.get(actualPosition));
    }

    // 修复图片加载方法（优化内存）
    private void loadImage(ImageView imageView, String url) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.banner_placeholder)
                .error(R.drawable.banner_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .dontTransform();

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);
    }

    private void loadImage(ImageView imageView, int resId) {
        Glide.with(imageView.getContext())
                .load(resId)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        if (bannerUrls == null || bannerUrls.isEmpty()) {
            return 1; // 显示占位图
        }
        // 修复：限制最大页面数（避免ANR问题）
        return isMultiImage ? Math.min(bannerUrls.size() * 10, MAX_PAGES) : bannerUrls.size();
    }

    @Override
    public void onViewRecycled(@NonNull BannerViewHolder holder) {
        // 修复：清除图片资源
        Glide.with(holder.bannerImage.getContext()).clear(holder.bannerImage);
        super.onViewRecycled(holder);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        // 修复：使用正确的变量名bannerImage（小写开头）
        ImageView bannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            // 修复：匹配布局文件中的ID（修正为bannerImage）
            bannerImage = itemView.findViewById(R.id.bannerImage);
        }
    }
}