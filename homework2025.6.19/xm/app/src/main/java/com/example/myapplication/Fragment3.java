package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

public class Fragment3 extends Fragment {

    private static final String TAG = "Fragment3";
    private ImageView imageView;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);

        imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.run5);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        button = new Button(getContext());
        button.setText("重复");
        button.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ViewGroup layout = (ViewGroup) view;
        layout.addView(imageView);
        layout.addView(button);

        view.post(new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        });

        button.setOnClickListener(v -> startAnimation());

        return view;
    }

    private void startAnimation() {
        imageView.clearAnimation();

        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scale = new ScaleAnimation(
                1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(2000);
        scale.setRepeatCount(2);
        scale.setRepeatMode(Animation.RESTART);

        RotateAnimation rotate = new RotateAnimation(
                0, -720,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(2);
        rotate.setRepeatMode(Animation.RESTART);

        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.8f);
        alpha.setDuration(2000);
        alpha.setRepeatCount(2);
        alpha.setRepeatMode(Animation.RESTART);

        animationSet.addAnimation(scale);
        animationSet.addAnimation(rotate);
        animationSet.addAnimation(alpha);

        animationSet.setFillAfter(true);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "animation start");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "animation end");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "animation repeat");
            }
        });

        imageView.startAnimation(animationSet);
    }
}