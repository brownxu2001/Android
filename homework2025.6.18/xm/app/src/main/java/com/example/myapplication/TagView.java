package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TagView extends AppCompatTextView {
    private static final int[] TAG_COLORS = {
            Color.parseColor("#009866"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#673AB7"),
            Color.parseColor("#03A9F4"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#795548")
    };

    private float lastX, lastY;
    private boolean isDragging = false;
    private long pressStartTime;
    private static final long LONG_PRESS_THRESHOLD = 200;

    public TagView(@NonNull Context context) {
        this(context, null);
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTextColor(Color.WHITE);
        setPadding(16, 8, 16, 8);
        setTextSize(14);

        setBackgroundColor(TAG_COLORS[new java.util.Random().nextInt(TAG_COLORS.length)]);
        setLongClickable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                pressStartTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isDragging && System.currentTimeMillis() - pressStartTime > LONG_PRESS_THRESHOLD) {
                    isDragging = true;
                    if (getParent() instanceof TagCloudLayout) {
                        ((TagCloudLayout) getParent()).setDragging(true);
                    }
                }

                if (isDragging) {
                    float deltaX = x - lastX;
                    float deltaY = y - lastY;

                    float newX = getX() + deltaX;
                    float newY = getY() + deltaY;

                    setX(newX);
                    setY(newY);

                    lastX = x;
                    lastY = y;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    isDragging = false;
                    if (getParent() instanceof TagCloudLayout) {
                        ((TagCloudLayout) getParent()).setDragging(false);
                    }
                }
                break;
        }

        return true;
    }
}