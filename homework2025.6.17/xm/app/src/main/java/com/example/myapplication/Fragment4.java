package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

public class Fragment4 extends Fragment {

    private ImageView imageView;
    private Button button;

    private class DecelerateCustomInterpolator implements TimeInterpolator {
        @Override
        public float getInterpolation(float input) {
            return input * input * input * input * input;
        }
    }

    private class ColorEvaluator implements TypeEvaluator<Integer> {
        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            int startRed = Color.red(startValue);
            int startGreen = Color.green(startValue);
            int startBlue = Color.blue(startValue);

            int endRed = Color.red(endValue);
            int endGreen = Color.green(endValue);
            int endBlue = Color.blue(endValue);

            int currentRed = (int)(startRed + fraction * (endRed - startRed));
            int currentGreen = (int)(startGreen + fraction * (endGreen - startGreen));
            int currentBlue = (int)(startBlue + fraction * (endBlue - startBlue));

            return Color.rgb(currentRed, currentGreen, currentBlue);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_4, container, false);

        imageView = view.findViewById(R.id.imageView);
        button = view.findViewById(R.id.button);

        button.setOnClickListener(v -> startAnimation());

        return view;
    }

    private void startAnimation() {
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(
                imageView, "rotationX", 0f, 360f
        );
        rotationX.setDuration(1000);
        rotationX.setInterpolator(new DecelerateCustomInterpolator());

        ObjectAnimator translationX = ObjectAnimator.ofFloat(
                imageView, "translationX", 0f, 120f
        );
        translationX.setDuration(1000);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(
                imageView, "alpha", 1f, 0.5f
        );
        alpha.setDuration(500);

        ObjectAnimator colorAnim = ObjectAnimator.ofObject(
                imageView,
                "backgroundColor",
                new ColorEvaluator(),
                Color.RED,
                Color.BLUE
        );
        colorAnim.setDuration(1500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotationX, translationX);
        animatorSet.play(alpha).after(translationX);
        animatorSet.play(colorAnim).after(alpha);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imageView.setRotationX(0f);
                imageView.setTranslationX(0f);
                imageView.setAlpha(1f);
                imageView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        animatorSet.start();
    }
}