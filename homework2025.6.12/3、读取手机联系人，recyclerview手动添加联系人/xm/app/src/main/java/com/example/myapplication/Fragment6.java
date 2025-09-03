package com.example.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Fragment6 extends Fragment {
    private ICalculate calculateService;
    private boolean isBound = false;
    private TextView resultText;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            calculateService = ICalculate.Stub.asInterface(service);
            isBound = true;
            Log.d("Fragment6", "服务已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            calculateService = null;
            isBound = false;
            Log.d("Fragment6", "服务断开");
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_6, container, false);
        resultText = view.findViewById(R.id.tv_result);
        Button btnCalculate = view.findViewById(R.id.btn_calculate);


        Intent intent = new Intent(requireContext(), RemoteService.class);
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);


        btnCalculate.setOnClickListener(v -> {
            if (isBound && calculateService != null) {
                try {
                    int result = calculateService.add(3, 5);
                    String output = "计算结果: " + result;
                    resultText.setText(output);
                    Log.d("Fragment6", output);
                } catch (RemoteException e) {
                    Log.e("Fragment6", "跨进程调用失败", e);
                    resultText.setText("计算失败");
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBound) {
            requireContext().unbindService(connection);
            Log.d("Fragment6", "服务已解绑");
        }
    }
}