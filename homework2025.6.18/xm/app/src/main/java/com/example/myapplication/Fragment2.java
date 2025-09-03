package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.animation.AnimatorInflater;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.fragment.app.Fragment;

public class Fragment2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);

        // 帧动画
        ImageView runImageView = view.findViewById(R.id.imageview);
        runImageView.setImageResource(R.drawable.run_ani);
        ((AnimationDrawable) runImageView.getDrawable()).start();

        // 平移动画
        Animation translation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_transation);
        view.findViewById(R.id.image_tran).startAnimation(translation);

        // 缩放动画
        Animation scale = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_scale);
        view.findViewById(R.id.image_scale).startAnimation(scale);

        // 属性动画（旋转X轴）
        ImageView propertyAnimTv = view.findViewById(R.id.image_animator);
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(
                getActivity(), R.animator.animator_rotate_x);
        animator.setTarget(propertyAnimTv);
        animator.start();

        return view;
    }
}