package com.example.music_xuzhaocheng.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.music_xuzhaocheng.R;
import com.example.music_xuzhaocheng.module.MusicInfo;

public class FloatingWindowManager {
    private Context context;
    private WindowManager windowManager;
    private View floatingView;

    private ImageView coverImage;
    private TextView songName;
    private TextView singerName;
    private ImageButton btnPlayPause;

    private boolean isPlaying = false;
    private MusicInfo currentMusic;
    private OnPlayPauseClickListener playPauseListener;

    public interface OnPlayPauseClickListener {
        void onPlayPauseClick(boolean isPlaying);
    }

    public FloatingWindowManager(Context context) {
        this.context = context.getApplicationContext();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void createFloatingWindow() {
        if (floatingView != null) return;

        // 加载悬浮窗布局
        floatingView = LayoutInflater.from(context).inflate(R.layout.floating_music_player, null);

        // 初始化视图
        coverImage = floatingView.findViewById(R.id.coverImage);
        songName = floatingView.findViewById(R.id.songName);
        singerName = floatingView.findViewById(R.id.singerName);
        btnPlayPause = floatingView.findViewById(R.id.btnPlayPause);

        // 设置布局参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.START;
        params.x = 0;
        params.y = 100;

        // 添加到窗口
        windowManager.addView(floatingView, params);

        // 设置点击事件
        btnPlayPause.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);

            if (playPauseListener != null) {
                playPauseListener.onPlayPauseClick(isPlaying);
            }
        });

        // 设置拖动功能
        final View dragView = floatingView.findViewById(R.id.floating_view);
        dragView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.8f);
                    return true;

                case MotionEvent.ACTION_UP:
                    v.setAlpha(1.0f);
                    return true;

                default:
                    return false;
            }
        });
    }

    public void updateMusicInfo(MusicInfo music) {
        if (music == null || floatingView == null) return;

        currentMusic = music;

        // 更新UI
        songName.setText(music.getMusicName());
        singerName.setText(music.getAuthor());

        // 这里应该使用Glide加载图片，由于上下文限制，暂时使用默认图标
        // Glide.with(context).load(music.getCoverUrl()).into(coverImage);
    }

    public void updatePlayState(boolean isPlaying) {
        this.isPlaying = isPlaying;
        if (btnPlayPause != null) {
            btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        }
    }

    public void setOnPlayPauseClickListener(OnPlayPauseClickListener listener) {
        this.playPauseListener = listener;
    }

    public void removeFloatingWindow() {
        if (floatingView != null && floatingView.getParent() != null) {
            windowManager.removeView(floatingView);
            floatingView = null;
        }
    }
}
