package com.example.music_xuzhaocheng;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.music_xuzhaocheng.Adapter.LyricsPagerAdapter;
import com.example.music_xuzhaocheng.module.MusicInfo;
import com.example.music_xuzhaocheng.utils.FloatingWindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayerActivity";
    public static final String ACTION_UPDATE_PLAYBACK = "com.example.music_xuzhaocheng.UPDATE_PLAYBACK";

    private ImageView coverImage;
    private TextView songName;
    private TextView singerName;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView totalTime;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private ImageButton btnPlayPause;
    private ImageButton btnPlayMode;
    private ImageButton btnClose;
    private ViewPager2 viewPager;
    private View playerView;
    private View lyricsView;
    private LyricsPagerAdapter adapter;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private ObjectAnimator rotationAnimator;
    private MusicInfo currentMusic;
    private List<MusicInfo> musicList = new ArrayList<>();
    private int currentPosition = 0;
    private int playMode = 0; // 0=顺序, 1=随机, 2=单曲
    private int[] modeIcons = {R.drawable.ic_play_mode_order, R.drawable.ic_play_mode_random, R.drawable.ic_play_mode_repeat};
    private FloatingWindowManager floatingWindowManager;
    private boolean isPlaying = false;


    private BroadcastReceiver playbackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_UPDATE_PLAYBACK)) {
                int command = intent.getIntExtra("command", -1);
                switch (command) {
                    case 0: // 播放/暂停
                        togglePlayPause();
                        break;
                    case 1: // 上一首
                        playPrev();
                        break;
                    case 2: // 下一首
                        playNext();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        currentPosition = getIntent().getIntExtra("currentPosition", 0);
        musicList = (List<MusicInfo>) getIntent().getSerializableExtra("musicList");
        currentMusic = musicList.get(currentPosition); // 初始化成员变量

//
//        MusicInfo currentMusic = musicList.get(currentPosition);
        adapter = new LyricsPagerAdapter(this, currentMusic.getLyricUrl());
        // 初始化视图
        initViews();

        // 注册广播接收器
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(playbackReceiver, new IntentFilter(ACTION_UPDATE_PLAYBACK));

        // 获取传递的数据
        Intent intent = getIntent();
        currentPosition = intent.getIntExtra("currentPosition", 0);
        musicList = (List<MusicInfo>) intent.getSerializableExtra("musicList");
        int moduleStyle = intent.getIntExtra("moduleStyle", 0);

        // 初始化媒体播放器
        initMediaPlayer();

        // 播放当前歌曲
        playMusic(currentPosition);

        // 在onCreate方法中
        ImageButton btnLike = findViewById(R.id.btnLike);

        btnLike.setOnClickListener(v -> {
//            MusicInfo currentMusic = musicList.get(currentPosition);
            boolean isLiked = !currentMusic.isLiked(); // 切换点赞状态

            // 更新UI
            btnLike.setImageResource(isLiked ?
                    R.drawable.ic_liked : R.drawable.ic_like);

            // 实际应用中应发送网络请求更新服务器状态
            currentMusic.setLiked(isLiked);

            Toast.makeText(this, isLiked ? "已点赞" : "已取消点赞",
                    Toast.LENGTH_SHORT).show();
        });
        initFloatingWindow();
    }

    private void initViews() {
        coverImage = findViewById(R.id.coverImage);
        songName = findViewById(R.id.songName);
        singerName = findViewById(R.id.singerName);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPlayMode = findViewById(R.id.btnPlayMode);
        btnClose = findViewById(R.id.btnClose);
        viewPager = findViewById(R.id.viewPager);
        playerView = findViewById(R.id.playerLayout);
        lyricsView = findViewById(R.id.lyricsLayout);

        // 设置ViewPager
        LyricsPagerAdapter adapter = new LyricsPagerAdapter(this, currentMusic.getLyricUrl());
        viewPager.setAdapter(adapter);

        // 设置滑动监听
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    playerView.setVisibility(View.VISIBLE);
                    lyricsView.setVisibility(View.GONE);
                } else {
                    playerView.setVisibility(View.GONE);
                    lyricsView.setVisibility(View.VISIBLE);
                }
            }
        });

        // 关闭按钮
        btnClose.setOnClickListener(v -> finish());

        // 播放控制按钮
        btnPrev.setOnClickListener(v -> playPrev());
        btnNext.setOnClickListener(v -> playNext());
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPlayMode.setOnClickListener(v -> changePlayMode());

        // 进度条监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    handler.post(updateSeekBar);
                }
            }
        });
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            // 自动播放下一首
            playNext();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) { // 匹配您在initFloatingWindow中使用的请求码
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    createFloatingWindow();
                }
            }
        }
    }
    private void playMusic(int position) {
        if (position < 0 || position >= musicList.size()) {
            Toast.makeText(this, "无效的播放位置", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPosition = position;
        MusicInfo music = musicList.get(position);

        // 更新UI
        songName.setText(music.getMusicName());
        singerName.setText(music.getAuthor());
        btnPlayPause.setImageResource(R.drawable.ic_pause);

        // 加载封面图片
        Glide.with(this)
                .asBitmap()
                .load(music.getCoverUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        coverImage.setImageBitmap(resource);

                        // 分析图片颜色
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                if (palette != null) {
                                    Palette.Swatch dominantSwatch = palette.getDominantSwatch();
                                    if (dominantSwatch != null) {
                                        int backgroundColor = dominantSwatch.getRgb();
                                        // 设置背景
                                        findViewById(R.id.rootLayout).setBackgroundColor(backgroundColor);
                                    }
                                }
                            }
                        });
                    }
                });

        // 开始旋转动画
        startCoverRotation();

        // 准备播放音乐
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getMusicUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                // 设置进度条最大值
                seekBar.setMax(mediaPlayer.getDuration());
                totalTime.setText(formatTime(mediaPlayer.getDuration()));

                // 开始播放
                mediaPlayer.start();

                // 更新进度条
                updateSeekBar = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            int currentPos = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentPos);
                            currentTime.setText(formatTime(currentPos));
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.post(updateSeekBar);
            });
        } catch (IOException e) {
            Log.e(TAG, "播放失败", e);
            Toast.makeText(this, "播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startCoverRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
        rotationAnimator = ObjectAnimator.ofFloat(coverImage, "rotation", 0f, 360f);
        rotationAnimator.setDuration(10000); // 10秒转一圈
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ObjectAnimator.RESTART);
        rotationAnimator.start();
    }

    private void stopCoverRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.pause();
        }
    }

    private void resumeCoverRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.resume();
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
            stopCoverRotation();
        } else {
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            resumeCoverRotation();
            handler.post(updateSeekBar);
        }
    }

    private void playPrev() {
        int prevPosition;
        if (playMode == 1) { // 随机模式
            prevPosition = getRandomPosition();
        } else if (playMode == 2) { // 单曲循环
            prevPosition = currentPosition;
        } else { // 顺序播放
            prevPosition = currentPosition - 1;
            if (prevPosition < 0) {
                prevPosition = musicList.size() - 1;
            }
        }
        playMusic(prevPosition);
    }

    private void playNext() {
        int nextPosition;
        if (playMode == 1) { // 随机模式
            nextPosition = getRandomPosition();
        } else if (playMode == 2) { // 单曲循环
            nextPosition = currentPosition;
        } else { // 顺序播放
            nextPosition = currentPosition + 1;
            if (nextPosition >= musicList.size()) {
                nextPosition = 0;
            }
        }
        playMusic(nextPosition);
    }

    private int getRandomPosition() {
        Random random = new Random();
        int newPosition;
        do {
            newPosition = random.nextInt(musicList.size());
        } while (newPosition == currentPosition && musicList.size() > 1);

        return newPosition;
    }

    private void changePlayMode() {
        playMode = (playMode + 1) % 3;
        btnPlayMode.setImageResource(modeIcons[playMode]);

        String modeText;
        switch (playMode) {
            case 0: modeText = "顺序播放"; break;
            case 1: modeText = "随机播放"; break;
            case 2: modeText = "单曲循环"; break;
            default: modeText = "未知模式";
        }
        Toast.makeText(this, modeText, Toast.LENGTH_SHORT).show();
    }
    private void initFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100); // 使用合适的请求码
            } else {
                createFloatingWindow();
            }
        } else {
            createFloatingWindow();
        }
    }
    private void createFloatingWindow() {
        floatingWindowManager = new FloatingWindowManager(this);
        floatingWindowManager.createFloatingWindow();

        // 如果有正在播放的音乐，更新信息
        if (currentMusic != null) {
            floatingWindowManager.updateMusicInfo(currentMusic);
            floatingWindowManager.updatePlayState(isPlaying);
        }
    }
    private void showFloatingWindow(MusicInfo music, boolean isPlaying) {
        this.currentMusic = music;
        this.isPlaying = isPlaying;

        if (floatingWindowManager == null) {
            createFloatingWindow();
        } else {
            floatingWindowManager.updateMusicInfo(music);
            floatingWindowManager.updatePlayState(isPlaying);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playbackReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 发送广播通知后台服务
        Intent intent = new Intent(MusicService.ACTION_UPDATE_PLAYBACK);
        intent.putExtra("command", 3); // 3=更新播放状态
        intent.putExtra("isPlaying", mediaPlayer != null && mediaPlayer.isPlaying());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}