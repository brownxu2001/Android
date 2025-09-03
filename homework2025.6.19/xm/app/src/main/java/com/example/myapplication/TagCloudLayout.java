package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TagCloudLayout extends ViewGroup {
    private static final int HORIZONTAL_SPACING = 16;
    private static final int VERTICAL_SPACING = 16;
    private final int horizontalSpacing;
    private final int verticalSpacing;
    private boolean isDragging = false;

    public TagCloudLayout(@NonNull Context context) {
        this(context, null);
    }

    public TagCloudLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagCloudLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        float density = getResources().getDisplayMetrics().density;
        this.horizontalSpacing = (int) (HORIZONTAL_SPACING * density);
        this.verticalSpacing = (int) (VERTICAL_SPACING * density);

        setClipChildren(false);
        setClipToPadding(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isDragging) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int lineWidth = 0;
        int lineHeight = 0;
        int totalHeight = 0;
        int maxWidth = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (lineWidth + childWidth > widthSize - getPaddingLeft() - getPaddingRight()) {
                maxWidth = Math.max(maxWidth, lineWidth);
                totalHeight += lineHeight + verticalSpacing;
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth + horizontalSpacing;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            if (i == count - 1) {
                maxWidth = Math.max(maxWidth, lineWidth);
                totalHeight += lineHeight;
            }
        }

        maxWidth += getPaddingLeft() + getPaddingRight();
        totalHeight += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(
                MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY ? widthSize : maxWidth,
                MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY ? heightSize : totalHeight
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isDragging) {
            return;
        }

        int width = getWidth();
        int currentX = getPaddingLeft();
        int currentY = getPaddingTop();
        int lineHeight = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (currentX + childWidth > width - getPaddingRight()) {
                currentX = getPaddingLeft();
                currentY += lineHeight + verticalSpacing;
                lineHeight = 0;
            }

            child.layout(currentX, currentY, currentX + childWidth, currentY + childHeight);

            currentX += childWidth + horizontalSpacing;
            lineHeight = Math.max(lineHeight, childHeight);
        }
    }

    public void setDragging(boolean dragging) {
        if (isDragging != dragging) {
            isDragging = dragging;
            if (!dragging) {
                requestLayout();
            }
        }
    }
}