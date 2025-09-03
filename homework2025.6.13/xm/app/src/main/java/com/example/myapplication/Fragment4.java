package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class Fragment4 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_4, container, false);

        view.findViewById(R.id.btn_send).setOnClickListener(v -> {
            Intent intent = new Intent("com.example.xm.STATIC_ACTION");
            intent.putExtra("msg", "测试静态广播数据");
            intent.setPackage(requireContext().getPackageName());
            requireContext().sendBroadcast(intent);
        });

        return view;
    }
}