package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment5 extends Fragment {
    private static final String[] TAGS = {
            "A", "B", "C", "D",
            "E", "F", "G", "H"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TagCloudLayout tagCloudLayout = new TagCloudLayout(getContext(), null);
        tagCloudLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        tagCloudLayout.setPadding(20, 20, 20, 20);

        for (String tag : TAGS) {
            TagView tagView = new TagView(getContext());
            tagView.setText(tag);
            tagView.setTextSize(14 + (int)(Math.random() * 8));
            tagCloudLayout.addView(tagView);
        }

        return tagCloudLayout;
    }
}