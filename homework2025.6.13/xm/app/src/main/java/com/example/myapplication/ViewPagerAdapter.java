package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Fragment1();
            case 1: return new Fragment2();
            case 2: return new Fragment3();
            case 3: return new Fragment4();
            case 4: return new Fragment5();
            case 5: return new Fragment6();
            case 6: return new Fragment7();
//           case 7: return new SearchFragment();  // 搜索功能
            case 8: return new Fragment9();
            case 9: return new Fragment10();
            default: return new Fragment1();
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}