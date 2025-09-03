package com.example.music_xuzhaocheng;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LyricsFragment extends Fragment {

    private static final String ARG_LYRIC_URL = "lyric_url";
    private TextView lyricsText;
    private Handler handler = new Handler();
    private Runnable updateLyrics;

    public static LyricsFragment newInstance(String lyricUrl) {
        LyricsFragment fragment = new LyricsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LYRIC_URL, lyricUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);
        lyricsText = view.findViewById(R.id.lyricsText);

        String lyricUrl = getArguments().getString(ARG_LYRIC_URL);
        loadLyrics(lyricUrl);

        updateLyrics = new Runnable() {
            @Override
            public void run() {
                // 实际应用中应根据播放进度更新歌词
                lyricsText.scrollBy(0, 1);
                handler.postDelayed(this, 100);
            }
        };
        handler.post(updateLyrics);
        return view;
    }

    private void loadLyrics(String lyricUrl) {
        if (lyricUrl == null || lyricUrl.isEmpty()) {
            lyricsText.setText("暂无歌词");
            return;
        }

        // 实际应用中应使用网络请求加载歌词
        // 这里使用伪代码表示
        new Thread(() -> {
            try {
                // 模拟网络请求
                Thread.sleep(500);
                String lyrics = "这是从网络加载的歌词内容...\n第二行歌词...";

                requireActivity().runOnUiThread(() ->
                        lyricsText.setText(lyrics));
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        lyricsText.setText("加载歌词失败"));
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateLyrics);
    }
}