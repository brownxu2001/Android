package com.example.music_xuzhaocheng.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.music_xuzhaocheng.MusicPlayerActivity;
import com.example.music_xuzhaocheng.R;
import com.example.music_xuzhaocheng.module.MusicInfo;
import com.example.music_xuzhaocheng.utils.FloatingWindowManager; // 引入悬浮窗管理类
import com.example.music_xuzhaocheng.MainActivity; // 引入首页Activity

import java.io.Serializable;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private static final int DEFAULT_PLACEHOLDER = R.drawable.music_placeholder;
    private List<MusicInfo> musicList;
    private final int layoutType;
    private final Context context;
    private final int moduleStyle; // 新增：记录模块类型
    private MusicInfo music;

    // 添加悬浮窗管理器引用
    private FloatingWindowManager floatingWindowManager;

    public SongAdapter(List<MusicInfo> musicList, int layoutType, Context context, int moduleStyle) {
        this.musicList = musicList;
        this.layoutType = layoutType;
        this.context = context;
        this.moduleStyle = moduleStyle; // 新增：保存模块类型

        // 如果是MainActivity实例，获取或创建悬浮窗管理器
        if (context instanceof MainActivity) {
            floatingWindowManager = ((MainActivity) context).getFloatingWindowManager();
        }
    }

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
        music = musicList.get(position);
        holder.songName.setText(music.getMusicName());
        holder.singerName.setText(music.getAuthor());

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

            // 点击+号时也显示悬浮窗
            showFloatingWindow(music);
        });

        // 整个项点击事件 - 修改为只触发悬浮窗显示
        holder.itemView.setOnClickListener(v -> {
            music = musicList.get(position);

            // 如果有MainActivity实例，通过Fragment显示播放器
            if (context instanceof MainActivity) {
                ((MainActivity) context).showMusicPlayer(music, true);
            }

            // 可选：启动完整的播放Activity
            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.putExtra("currentPosition", position);
            intent.putExtra("musicList", (Serializable) musicList);
            intent.putExtra("moduleStyle", moduleStyle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    // 新增方法：显示悬浮窗
    private void showFloatingWindow(MusicInfo music) {
        if (floatingWindowManager != null && music != null) {
            floatingWindowManager.updateMusicInfo(music);
            floatingWindowManager.updatePlayState(true); // 默认显示为播放状态

            // 如果需要跳转到播放页面，保留原有逻辑
            // Intent intent = new Intent(context, MusicPlayerActivity.class);
            // intent.putExtra("currentPosition", getAdapterPosition());
            // intent.putExtra("musicList", (Serializable) musicList);
            // intent.putExtra("moduleStyle", moduleStyle);
            // context.startActivity(intent);
        }
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

            if (layoutType == 4) {
                songName.setTextSize(14);
                singerName.setTextSize(12);
            }
        }
    }
}
