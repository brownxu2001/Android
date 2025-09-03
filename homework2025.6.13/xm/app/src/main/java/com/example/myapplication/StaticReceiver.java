package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StaticReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.xm.STATIC_ACTION".equals(intent.getAction())) {
            String msg = intent.getStringExtra("msg");
            Log.d("STATIC", "Fragment4收到静态广播: " + msg);
        }
    }
}