package com.muy.uisample.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.muy.uisample.R;
import com.muy.uisample.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 2019-11-30.
 * Desc:
 */
public class FlowLayout extends ViewGroup {

    private int mColumnSpace;
    private int mRowSpace;
    private int mMaxLines; // 最多显示的行数，0代表不做限制

    private List<List<View>> mAllRowsViews;
    private List<Integer> mAllRowsHeights;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mColumnSpace = a.getDimensionPixelSize(R.styleable.FlowLayout_column_space, 0);
        mRowSpace = a.getDimensionPixelSize(R.styleable.FlowLayout_row_space, 0);
        mMaxLines = a.getInteger(R.styleable.FlowLayout_maxLines, 0);
        // 记得一定要调用recycle()
        a.recycle();
    }

    /**
     * 设置列间距值
     *
     * @param size 列间距值（单位为px）
     */
    public void setColumnSpace(int size) {
        mColumnSpace = size;
    }

    /**
     * 设置行间距值
     *
     * @param size 行间距值（单位为px）
     */
    public void setRowSpace(int size) {
        mRowSpace = size;
    }

    /**
     * 设置最多显示行数
     *
     * @param maxLines
     */
    public void setMaxLines(int maxLines) {
        mMaxLines = maxLines;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 可能会被调用多次，需要重新初始化
        mAllRowsViews = new ArrayList<>();
        mAllRowsHeights = new ArrayList<>();

        // 获取当前view的模式（mode）和宽高值（size）
        int widthSie = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);


        // 如果是EXACTLY，最大宽度为当前view的宽度，否则，就是手机的屏幕宽度
        int maxWidthSize = widthMode == MeasureSpec.EXACTLY ? widthSie
                : UIUtils.getScreenWidth((Activity) getContext());

        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();

        // 记录当前行已经使用的宽度
        int usedWidth = 0;
        // 记录当前的总高度
        int totalHeight = 0;
        // 记录当前行子view的最大高度
        int maxChildHeight = 0;

        int childCount = getChildCount();

        ArrayList<View> childViews = new ArrayList<>();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            // 需要根据父view的MeasureSpec来得到子view的MeasureSpec
            LayoutParams lp = child.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, widthPadding, lp.width);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding, lp.height);
            // 设置子view的尺寸
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int columnSpace = i > 0 ? mColumnSpace : 0;
            usedWidth += columnSpace + childWidth;

            if (usedWidth > maxWidthSize) {
                // 超过一行，需要换行
                mAllRowsViews.add(childViews);
                mAllRowsHeights.add(maxChildHeight);

                if (mMaxLines > 0 && mAllRowsViews.size() == mMaxLines) {
                    // 已达到最多显示的行数，舍弃多余的view
                    break;
                }
                childViews = new ArrayList<>();

                totalHeight += mRowSpace + maxChildHeight;
                maxChildHeight = childHeight;
                usedWidth = childWidth;
            } else {
                // 获取当前行child高度最大值
                maxChildHeight = Math.max(childHeight, maxChildHeight);
            }

            childViews.add(child);
        }
        // 加上最后一行子view中最大高度
        totalHeight += maxChildHeight;

        // 确认当前view的高度
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        totalHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : (totalHeight + heightPadding);
        // 记录当前view的宽高值
        setMeasuredDimension(maxWidthSize, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int rowNum = mAllRowsViews.size();

        int left = getPaddingLeft();
        int top = getPaddingTop();

        for (int i = 0; i < rowNum; i++) {
            List<View> rowViews = mAllRowsViews.get(i);
            int height = mAllRowsHeights.get(i);

            for (View child : rowViews) {
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
                left = right + mColumnSpace;
            }

            left = getPaddingLeft();
            top += height + mRowSpace;
        }
    }
}
