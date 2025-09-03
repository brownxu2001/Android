package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class RemoteService extends Service {
    private static final String TAG = "RemoteService";

    private final ICalculate.Stub binder = new ICalculate.Stub() {
        @Override
        public int add(int a, int b) throws RemoteException {
            Log.d(TAG, "计算: " + a + " + " + b);
            return a + b;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}