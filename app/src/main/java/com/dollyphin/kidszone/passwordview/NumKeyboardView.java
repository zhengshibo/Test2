/*Top Secret*/
package com.dollyphin.kidszone.passwordview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by shibo.zheng on 2016/11/28.
 */
public class NumKeyboardView extends LinearLayout implements View.OnClickListener {
    private final int COLOR_WHITE = 1;
    private int mColorStyle = COLOR_WHITE;
    private int DESWIDTH = 500;
    private int DESHEIGHT = getResources().getDimensionPixelOffset(R.dimen.num_keyboard_height);
    private int mCellSpacing = 1;
    private int mRows = 3;
    private int mColumns = 4;
    private KeyboardListener mListener;

    private String[] mNum = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "c", "0", "ok"};
    private int[] mWhiteImages = new int[]{R.drawable.num_1, R.drawable.num_2, R.drawable.num_3, R.drawable.num_4, R.drawable.num_5,
            R.drawable.num_6, R.drawable.num_7, R.drawable.num_8, R.drawable.num_9, R.drawable.num_c, R.drawable.num_0, R.drawable.ok};

    public NumKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumKeyboardView);
        int index = a.getInt(R.styleable.NumKeyboardView_color_style, -1);
        if (index >= 0) {
            setColorStyle(index);
        }
        init(context, -1);
    }

    public void init(int count) {
        init(getContext(), count);
    }

    private void init(Context context, int count) {
        try {
            this.removeAllViews();
            View view;
            for (int i = 0; i < mWhiteImages.length + count; i++) {
                view = View.inflate(context, R.layout.item_num_keyboard, null);
                view.setOnClickListener(this);
                addView(view);
                view = null;
            }
            synchronized (this) {
                notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = DESWIDTH;
        int desiredHeight = DESHEIGHT;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        Drawable background = getBackground();

        int backgroundWidth = background.getIntrinsicWidth();
        int backgroundHeight = background.getIntrinsicHeight();

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(backgroundWidth, Math.min(desiredWidth, widthSize));
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(backgroundHeight, Math.min(desiredHeight, heightSize));
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if (!changed) {
//            return;
//        }
        try {
            int width = r - l;
            int height = b - t;
            KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "onLayout: width: " + width + "   height:" + height);
            int cellWidth = (width - mCellSpacing * (mRows + 1)) / mRows;
            int cellHeight = (height - mCellSpacing * (mRows + 1)) / mColumns;
            int left = 1;
            int top = 0;
            int length = getChildCount();
            NumView child;
            for (int i = 1; i <= length; i++) {
                child = (NumView) getChildAt(i - 1);
                KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "onLayout: child  " + child + "    " + i);
                child.layout(left, top, left + cellWidth, top + cellHeight);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                params.width = cellWidth;
                params.height = cellHeight;
                child.setLayoutParams(params);
                child.invalidate();

                child.setmStr(mNum[i - 1]);
                if (mColorStyle == COLOR_WHITE) {
                    child.setmNumDrawable(getResources().getDrawable(mWhiteImages[i - 1]));
                }
                left += cellWidth + mCellSpacing;
                if (i % mRows == 0) {
                    left = 1;
                    top += cellHeight + mCellSpacing;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void setColorStyle(int colorStyle) {
        this.mColorStyle = colorStyle;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof NumView) {
            String s = ((NumView) view).getmStr();
            if (null != mListener) {
                if (KidsZoneUtil.isNumeric(s)) {
                    mListener.onClickNum(s);
                } else if (s.equals("ok")) {
                    mListener.onClickOk();
                } else {
                    mListener.onClear();
                }
            }
        }
    }

    public void setKeyBoardListener(KeyboardListener l) {
        this.mListener = l;
    }

    public interface KeyboardListener {
        void onClickNum(String s);

        void onClear();

        void onClickOk();
    }
}
