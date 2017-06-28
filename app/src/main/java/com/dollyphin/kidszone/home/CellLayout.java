/*Top Secret*/
package com.dollyphin.kidszone.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class CellLayout extends ViewGroup {
    int N = 0;
    int L = 0;
    int width = 0;
    int height = 0;
    int widthSpecSize = 0;
    int heightSpecSize = 0;
    final int cellWidth;
    final int cellHeight;

    final int cellPaddingStart;
    final int cellPaddingTop;
    final int cellPaddingBottom;

    int cellSpaceX;
    int cellSpaceY;

    public CellLayout(Context context) {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        N = KidsZoneUtil.getCellX(context);
        L = KidsZoneUtil.getCellY(context);

        int width = context.getResources().getDimensionPixelOffset(R.dimen.home_width);
        int height = context.getResources().getDimensionPixelOffset(R.dimen.home_height);
        widthSpecSize = width;//1024;//KidsZoneUtil.getScreenWidth(context)+KidsZoneUtil.getNavigationBarHeight(context);
        heightSpecSize = height;//552;//KidsZoneUtil.getScreenHeight(context)-getResources().getDimensionPixelOffset(R.dimen.cell_space_bottom);
        cellWidth = context.getResources().getDimensionPixelOffset(R.dimen.cell_width);
        cellHeight = context.getResources().getDimensionPixelOffset(R.dimen.icon_size) +
                context.getResources().getDimensionPixelOffset(R.dimen.txt_size_18) +
                context.getResources().getDimensionPixelOffset(R.dimen.icon_text_paddding);
        cellPaddingStart = context.getResources().getDimensionPixelOffset(R.dimen.cell_icon_padding);
        cellPaddingTop = context.getResources().getDimensionPixelOffset(R.dimen.cell_top);
        cellPaddingBottom = context.getResources().getDimensionPixelOffset(R.dimen.cell_bottom);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            int childLeft = getPaddingStart() + cellPaddingStart + (i % N) * (cellWidth + cellSpaceX);
            int childTop = getPaddingTop() + cellPaddingTop + (i / N) * (cellHeight + cellSpaceY);

            child.layout(childLeft, childTop,
                    childLeft + cellWidth, childTop + cellHeight);
            KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "onLayout=i=>" + i + "=width=>" + child.getWidth());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        setMeasuredDimension(widthSpecSize, heightSpecSize);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        cellSpaceX = (width - (cellPaddingStart * 2) - (N * cellWidth)) / (N - 1);
        cellSpaceY = (height - (cellPaddingTop + cellPaddingBottom) - L * cellHeight) / (L - 1);

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child);
        }
    }

    public void measureChild(View child) {
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY);
        int childheightMeasureSpec = MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY);

        child.measure(childWidthMeasureSpec, childheightMeasureSpec);
    }
}
