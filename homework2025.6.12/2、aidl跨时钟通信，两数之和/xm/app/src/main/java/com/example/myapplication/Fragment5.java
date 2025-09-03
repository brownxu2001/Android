//package com.example.myapplication;
//
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//
//public class Fragment5 extends Fragment {
//    private static final String DYNAMIC_ACTION = "com.example.myapplication.DYNAMIC_ACTION";
//    private DynamicReceiver receiver;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_6, container, false);
//
//
//        view.findViewById(R.id.btn_send).setOnClickListener(v -> {
//            Intent intent = new Intent(DYNAMIC_ACTION);
//            intent.putExtra("msg", "测试动态广播数据");
//            requireContext().sendBroadcast(intent);
//            Log.d("Fragment6", "已发送广播");
//        });
//
//        return view;
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        receiver = new DynamicReceiver();
//        IntentFilter filter = new IntentFilter(DYNAMIC_ACTION);
//        requireContext().registerReceiver(receiver, filter);
//        Log.d("Fragment6", "广播接收器已注册");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        requireContext().unregisterReceiver(receiver);
//
//    }
//}
package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class Fragment5 extends Fragment {
    private BroadcastReceiver receiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_5, container, false);

        view.findViewById(R.id.btn_send).setOnClickListener(v -> {

            Intent intent = new Intent("DYNAMIC_ACTION");
            intent.putExtra("data", "测试数据");
            requireContext().sendBroadcast(intent);
            Log.d("Fragment5", "已发送广播");
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Fragment5", "收到广播：" + intent.getAction());
                if (intent.getExtras() != null) {
                    Log.d("Fragment5", "附加数据：" + intent.getStringExtra("data"));
                }
            }
        };

        IntentFilter filter = new IntentFilter("DYNAMIC_ACTION");
        requireContext().registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (receiver != null) {
            requireContext().unregisterReceiver(receiver);
            Log.d("Fragment5", "已注销广播接收器");
        }
    }
}