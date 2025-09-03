package com.example.music_xuzhaocheng.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.music_xuzhaocheng.LyricsFragment;
import com.example.music_xuzhaocheng.PlayerFragment;

public class LyricsPagerAdapter extends FragmentStateAdapter {

    private final String lyricUrl;

    public LyricsPagerAdapter(@NonNull FragmentActivity fragmentActivity, String lyricUrl) {
        super(fragmentActivity);
        this.lyricUrl = lyricUrl;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PlayerFragment();
        } else {
            return LyricsFragment.newInstance(lyricUrl);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}