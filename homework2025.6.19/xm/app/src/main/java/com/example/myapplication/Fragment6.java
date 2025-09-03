package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.Fragment6Binding;

public class Fragment6 extends Fragment {
    private Fragment6Binding binding;
    private GameViewModel1 viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 使用DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_6, container, false);
        binding.setLifecycleOwner(this);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(GameViewModel1.class);
        binding.setViewModel(viewModel);

        // 设置搜索按钮点击事件
        binding.searchButton.setOnClickListener(v -> {
            String gameId = binding.gameIdInput.getText().toString().trim();
            if (!gameId.isEmpty()) {
                viewModel.fetchGameInfo(gameId);
            }
        });

        // 观察数据变化
        observeViewModel();

        return binding.getRoot();
    }

    private void observeViewModel() {
        viewModel.getGameInfo().observe(getViewLifecycleOwner(), gameInfo -> {
            if (gameInfo != null) {
                // 更新UI显示游戏信息
                binding.gameName.setText(gameInfo.getGameName());
                binding.gameDescription.setText(gameInfo.getIntroduction());
                // 其他UI更新...
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
}