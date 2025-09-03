package com.example.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;


public class MainActivity extends Activity {

    /** 1. 静态变量持有 Activity 引用（泄漏）*/
    public static MainActivity staticRef;

    /** 2. 注册广播但未注销（泄漏） */
    private MyReceiver myReceiver;

    /** 4. 非静态 Handler（泄漏） */
    private Handler leakHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLeakStatic).setOnClickListener(v -> leakByStaticRef());
        findViewById(R.id.btnLeakReceiver).setOnClickListener(v -> leakByReceiver());
        findViewById(R.id.btnLeakThread).setOnClickListener(v -> leakByThread());
        findViewById(R.id.btnLeakHandler).setOnClickListener(v -> leakByHandler());
    }

    // 1 静态变量泄漏
    private void leakByStaticRef() {
        // 错误：Activity 被 static 引用持有，导致无法释放
        staticRef = this;

        // 解法：
        // staticRef = null;
        // 或者避免将 Context（尤其是 Activity）赋值给 static 变量
    }

    // 2 广播未注销泄漏
    private void leakByReceiver() {
        // 错误：注册后未注销，Activity 退出后仍被系统引用
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter("com.example.DUMMY_ACTION");
        registerReceiver(myReceiver, filter);

        // 解法：（在 onDestroy() 中注销）：
        // if (myReceiver != null) {
        //     unregisterReceiver(myReceiver);
        //     myReceiver = null;
        // }
    }

    // 3 线程持有外部类泄漏
    private void leakByThread() {
        // 错误：匿名内部类/非静态内部类隐式持有外部 Activity 引用
        new LeakThread().start();

        // 解法：使用静态内部类 + WeakReference<Activity>
        // new SafeThread(this).start();
    }

    private class LeakThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(10 * 60 * 1000); // 模拟长任务
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // 4 Handler 延迟任务导致泄漏
    private void leakByHandler() {
        // 错误：非静态 Handler 隐式持有外部类引用（MainActivity）
        leakHandler.postDelayed(() -> {

        }, 10 * 60 * 1000); // 10分钟

        // 解法：
        // - 使用静态 Handler + WeakReference
        // - 或在 onDestroy() 中移除任务：
        // leakHandler.removeCallbacksAndMessages(null);
    }



    // 广播接收器
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 无操作
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //  故意不释放资源

        // 解法：
        // if (myReceiver != null) {
        //     unregisterReceiver(myReceiver);
        //     myReceiver = null;
        // }
        // leakHandler.removeCallbacksAndMessages(null);
        // staticRef = null;
    }
}
