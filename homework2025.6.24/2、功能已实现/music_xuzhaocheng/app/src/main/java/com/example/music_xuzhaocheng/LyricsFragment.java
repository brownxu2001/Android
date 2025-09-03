package com.example.music_xuzhaocheng;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_xuzhaocheng.Adapter.LyricsAdapter;
import com.example.music_xuzhaocheng.module.LrcLine;
import com.example.music_xuzhaocheng.utils.LrcParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class LyricsFragment extends Fragment {

    private static final String ARG_LYRIC_URL = "lyric_url";
    private RecyclerView recyclerView;
    private LyricsAdapter adapter;
    private List<LrcLine> lrcLines;
    private Handler handler = new Handler();

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
        recyclerView = view.findViewById(R.id.lyrics_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String lyricUrl = getArguments().getString(ARG_LYRIC_URL);
        loadLyrics(lyricUrl);

        return view;
    }

    private void loadLyrics(String lyricUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(lyricUrl);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                lrcLines = LrcParser.parse(content.toString());

                requireActivity().runOnUiThread(() -> {
                    adapter = new LyricsAdapter(lrcLines);
                    recyclerView.setAdapter(adapter);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 播放器定期调用这个方法同步歌词
    public void updateCurrentTime(long currentTimeMs) {
        if (lrcLines == null || adapter == null) return;
        for (int i = 0; i < lrcLines.size(); i++) {
            if (currentTimeMs < lrcLines.get(i).getTime()) {
                int highlight = i - 1;
                adapter.setHighlightPosition(highlight);
                recyclerView.scrollToPosition(Math.max(highlight, 0));
                break;
            }
        }
    }
}
