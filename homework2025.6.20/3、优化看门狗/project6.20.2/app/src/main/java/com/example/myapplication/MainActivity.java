package com.example.myapplication;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class MainActivity extends Activity {

    private ANRWatchDog anrWatchDog;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 启动 ANR 监控
        anrWatchDog = new ANRWatchDog();
        anrWatchDog.start();

        Log.d("MainActivity", "App started, WatchDog started");

        // 3 秒后模拟主线程阻塞 10 秒
        mainHandler.postDelayed(() -> {
            Log.d("MainActivity", "模拟主线程阻塞 10 秒");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 3000);
    }
}
