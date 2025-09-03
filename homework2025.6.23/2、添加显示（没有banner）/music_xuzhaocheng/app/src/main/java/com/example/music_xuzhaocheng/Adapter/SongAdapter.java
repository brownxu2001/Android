package com.example.music_xuzhaocheng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_xuzhaocheng.R;
import com.example.music_xuzhaocheng.module.MusicInfo;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private static final int DEFAULT_PLACEHOLDER = R.drawable.music_placeholder;
    private List<MusicInfo> musicList;
    private final int layoutType;
    private final Context context;

    public SongAdapter(List<MusicInfo> musicList, int layoutType, Context context) {
        this.musicList = musicList;
        this.layoutType = layoutType;
        this.context = context;
    }

    // 添加新数据的方法（用于上拉加载）
    public void addMoreData(List<MusicInfo> newData) {
        if (newData != null && !newData.isEmpty()) {
            int startPosition = musicList.size();
            musicList.addAll(newData);
            notifyItemRangeInserted(startPosition, newData.size());
        }
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        if (layoutType == 2) {
            layoutRes = R.layout.item_song_horizontal;
        } else if (layoutType == 4) {
            layoutRes = R.layout.item_song_double;
        } else {
            layoutRes = R.layout.item_song_single;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new SongViewHolder(view, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        MusicInfo music = musicList.get(position);
        holder.songName.setText(music.getMusicName());
        holder.singerName.setText(music.getAuthor());

        // 加载封面图
        Glide.with(holder.itemView.getContext())
                .load(music.getCoverUrl())
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_PLACEHOLDER)
                .into(holder.coverImage);

        // +号按钮点击事件
        holder.addButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(),
                    "将 " + music.getMusicName() + " 添加到音乐列表",
                    Toast.LENGTH_SHORT).show();
        });

        // 整个项点击事件
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(),
                    music.getMusicName(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songName, singerName;
        ImageView coverImage;
        ImageButton addButton;

        public SongViewHolder(@NonNull View itemView, int layoutType) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.coverImage);
            songName = itemView.findViewById(R.id.songName);
            singerName = itemView.findViewById(R.id.singerName);
            addButton = itemView.findViewById(R.id.addButton);

            // 根据不同布局类型调整UI元素
            if (layoutType == 4) {
                // 一行两列布局，调整元素大小
                songName.setTextSize(14);
                singerName.setTextSize(12);
            }
        }
    }
}