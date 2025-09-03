package com.example.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ANR_DEMO";
    private static final String ANR_BROADCAST_ACTION = "com.example.myapplication.ANR_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerAnrReceiver();

        Button btnTrigger = findViewById(R.id.btn_trigger);
        btnTrigger.setOnClickListener(v -> triggerAllAnrs());
    }

    private void registerAnrReceiver() {
        IntentFilter filter = new IntentFilter(ANR_BROADCAST_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                    new AnrReceiver(),
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            registerReceiver(new AnrReceiver(), filter);
        }
        Log.d(TAG, "BroadcastReceiver registered");
    }

    private void triggerAllAnrs() {
        // 1. Activity ANR
        Log.d(TAG, "Activity ANR triggered at: " + SystemClock.uptimeMillis());
        SystemClock.sleep(20000);
        Log.d(TAG, "Activity ANR finished at: " + SystemClock.uptimeMillis());

        // 2. Service ANR
        startService(new Intent(this, AnrService.class));

        // 3. BroadcastReceiver ANR
        Log.d(TAG, "Sending broadcast...");
        sendBroadcast(new Intent(ANR_BROADCAST_ACTION));
    }

    // ANR Service
    public static class AnrService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "Service ANR triggered at: " + SystemClock.uptimeMillis());
            SystemClock.sleep(20000);
            Log.d(TAG, "Service ANR finished at: " + SystemClock.uptimeMillis());
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    // ANR BroadcastReceiver
    public static class AnrReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "BroadcastReceiver ANR triggered at: " + SystemClock.uptimeMillis());
            SystemClock.sleep(20000);
            Log.d(TAG, "BroadcastReceiver ANR finished at: " + SystemClock.uptimeMillis());
        }
    }
}