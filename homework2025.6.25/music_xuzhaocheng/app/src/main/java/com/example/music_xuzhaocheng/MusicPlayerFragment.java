package com.example.music_xuzhaocheng;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.music_xuzhaocheng.module.MusicInfo;
import com.bumptech.glide.Glide;

public class MusicPlayerFragment extends Fragment {
    private ImageView coverImage;
    private TextView songName;
    private TextView singerName;
    private ImageButton btnPlayPause;

    private boolean isPlaying = false;
    private MusicInfo currentMusic;

    // 接口用于处理播放/暂停事件
    public interface OnPlayPauseClickListener {
        void onPlayPauseClick(boolean isPlaying);
    }

    private OnPlayPauseClickListener playPauseListener;

    public void setOnPlayPauseClickListener(OnPlayPauseClickListener listener) {
        this.playPauseListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化视图
        coverImage = view.findViewById(R.id.coverImage);
        songName = view.findViewById(R.id.songName);
        singerName = view.findViewById(R.id.singerName);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);

        // 设置点击事件
        btnPlayPause.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);

            if (playPauseListener != null) {
                playPauseListener.onPlayPauseClick(isPlaying);
            }
        });
    }

    public void updateMusicInfo(MusicInfo music) {
        if (music == null || getView() == null) return;

        currentMusic = music;

        // 更新UI
        songName.setText(music.getMusicName());
        singerName.setText(music.getAuthor());

        // 使用Glide加载图片
        Glide.with(this)
                .load(music.getCoverUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(coverImage);
    }

    public void updatePlayState(boolean isPlaying) {
        this.isPlaying = isPlaying;
        if (btnPlayPause != null) {
            btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        }
    }
}
