package com.example.myapplication;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.util.Map;

public class ANRWatchDog {

    private static final String TAG = "ANRWatchDog";
    private static final int CHECK_INTERVAL_MS = 1000;
    private static final int MAX_MISSED = 5;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Handler watchDogHandler;
    private volatile boolean responded = true;
    private int missedCount = 0;

    public ANRWatchDog() {
        HandlerThread thread = new HandlerThread("WatchDogThread");
        thread.start();
        watchDogHandler = new Handler(thread.getLooper());
    }

    public void start() {
        Log.d(TAG, "WatchDog started");
        watchDogHandler.postDelayed(checkRunnable, CHECK_INTERVAL_MS);
    }

    private final Runnable heartbeat = () -> {
        responded = true;
        Log.d(TAG, "主线程响应 heartbeat");
    };

    private final Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {
            responded = false;
            mainHandler.post(heartbeat);

            // 0.5 秒后检查主线程是否执行了任务
            watchDogHandler.postDelayed(() -> {
                if (!responded) {
                    missedCount++;
                    Log.w(TAG, "Missed heartbeat #" + missedCount);
                } else {
                    Log.d(TAG, "主线程正常响应，missedCount 清零");
                    missedCount = 0;
                }

                if (missedCount >= MAX_MISSED) {
                    Log.e(TAG, "Detected potential ANR. Dumping all threads...");
                    dumpAllThreads();
                    missedCount = 0;
                }

                watchDogHandler.postDelayed(this, CHECK_INTERVAL_MS); // 循环检查
            }, 500);
        }
    };

    private void dumpAllThreads() {
        Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : traces.entrySet()) {
            Thread t = entry.getKey();
            Log.e(TAG, "Thread: " + t.getName() + " (State: " + t.getState() + ")");
            for (StackTraceElement e : entry.getValue()) {
                Log.e(TAG, "    at " + e.toString());
            }
        }
    }
}
