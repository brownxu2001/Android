package com.example.music_xuzhaocheng;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.music_xuzhaocheng.module.MusicInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    private static final String TAG = "MusicService";
    public static final String ACTION_UPDATE_PLAYBACK = "com.example.music_xuzhaocheng.UPDATE_PLAYBACK";

    private MediaPlayer mediaPlayer;
    private List<MusicInfo> musicList;
    private int currentPosition = 0;
    private int playMode = 0; // 0=顺序, 1=随机, 2=单曲

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
                    case 3: // 更新播放状态
                        updatePlaybackState();
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(playbackReceiver, new IntentFilter(ACTION_UPDATE_PLAYBACK));

        mediaPlayer.setOnCompletionListener(mp -> playNext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            musicList = (List<MusicInfo>) intent.getSerializableExtra("musicList");
            currentPosition = intent.getIntExtra("currentPosition", 0);
            playMusic(currentPosition);
        }
        return START_STICKY;
    }

    private void playMusic(int position) {
        if (musicList == null || position < 0 || position >= musicList.size()) {
            return;
        }

        currentPosition = position;
        MusicInfo music = musicList.get(position);

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getMusicUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                notifyPlaybackState(true);
            });
        } catch (IOException e) {
            Log.e(TAG, "后台播放失败", e);
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            notifyPlaybackState(false);
        } else {
            mediaPlayer.start();
            notifyPlaybackState(true);
        }
    }

    private void playPrev() {
        int prevPosition;
        if (playMode == 1) {
            prevPosition = getRandomPosition();
        } else if (playMode == 2) {
            prevPosition = currentPosition;
        } else {
            prevPosition = currentPosition - 1;
            if (prevPosition < 0) {
                prevPosition = musicList.size() - 1;
            }
        }
        playMusic(prevPosition);
    }

    private void playNext() {
        int nextPosition;
        if (playMode == 1) {
            nextPosition = getRandomPosition();
        } else if (playMode == 2) {
            nextPosition = currentPosition;
        } else {
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

    private void updatePlaybackState() {
        notifyPlaybackState(mediaPlayer.isPlaying());
    }

    private void notifyPlaybackState(boolean isPlaying) {
        Intent intent = new Intent(MusicPlayerActivity.ACTION_UPDATE_PLAYBACK);
        intent.putExtra("isPlaying", isPlaying);
        intent.putExtra("currentPosition", currentPosition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playbackReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}