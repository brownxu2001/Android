package com.example.music_xuzhaocheng;

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
import com.example.music_xuzhaocheng.MusicInfo;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<MusicInfo> musicList;
    private int layoutType; // 2:横滑大卡 3:一行一列 4:一行两列

    public SongAdapter(List<MusicInfo> musicList, int layoutType) {
        this.musicList = musicList;
        this.layoutType = layoutType;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        if (layoutType == 2) {
            layoutRes = R.layout.item_song_horizontal;
        } else if (layoutType == 4) {
            layoutRes = R.layout.item_song_double;
        } else { // 默认一行一列
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
                .placeholder(R.drawable.music_placeholder)
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
        return musicList.size();
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