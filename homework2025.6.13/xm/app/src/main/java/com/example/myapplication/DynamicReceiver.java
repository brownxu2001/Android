package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DynamicReceiver extends BroadcastReceiver {
    private static final String TAG = "DynamicReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "动作: " + intent.getAction());
        Log.d(TAG, "附加数据: " + intent.getStringExtra("msg"));
    }
}