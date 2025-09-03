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
    private static final int MAX_PAGES = 100;

    public BannerPagerAdapter(List<String> bannerUrls) {
        this.bannerUrls = bannerUrls;
        this.isMultiImage = bannerUrls != null && bannerUrls.size() > 1;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            return 1;
        }

        return isMultiImage ? Math.min(bannerUrls.size() * 10, MAX_PAGES) : bannerUrls.size();
    }

    @Override
    public void onViewRecycled(@NonNull BannerViewHolder holder) {

        Glide.with(holder.bannerImage.getContext()).clear(holder.bannerImage);
        super.onViewRecycled(holder);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        ImageView bannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
        }
    }
}