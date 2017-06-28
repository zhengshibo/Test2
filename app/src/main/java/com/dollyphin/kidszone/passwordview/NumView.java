/*Top Secret*/
package com.dollyphin.kidszone.passwordview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.dollyphin.kidszone.util.KidsZoneLog;


/**
 * Created by shibo.zheng on 2016/11/28.
 */
public class NumView extends View {
    private String mStr;
    private Drawable mNumDrawable;
    private Drawable mBg;

    public NumView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int measuredWidth = getWidth() - getPaddingStart() - getPaddingEnd();
        int measuredHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        Rect rect = new Rect(getPaddingStart(), getPaddingTop(), getPaddingStart() + measuredWidth, getPaddingTop() + measuredHeight);
        if (mBg != null) {
            int width = mBg.getIntrinsicWidth();
            int height = mBg.getIntrinsicHeight();
            int left = rect.left + (measuredWidth - width) / 2;
            int top = rect.top + (measuredHeight - height) / 2;
            mBg.setBounds(left, top, left + width, top + height);
            mBg.draw(canvas);
        }
        if (mNumDrawable != null) {
            int width = mNumDrawable.getIntrinsicWidth();
            int height = mNumDrawable.getIntrinsicHeight();
            int left = rect.left + (measuredWidth - width) / 2;
            int top = rect.top + (measuredHeight - height) / 2;
            KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "onDraw: measuredWidth == " + measuredWidth + "  measuredHeight == " + measuredHeight + "  left == " + left + "   top == " + top);
            mNumDrawable.setBounds(left, top, left + width, top + height);
            mNumDrawable.draw(canvas);
        }
    }

    public String getmStr() {
        return mStr;
    }

    public void setmStr(String mStr) {
        this.mStr = mStr;
        invalidate();
    }

    public Drawable getmNumDrawable() {
        return mNumDrawable;
    }

    public void setmNumDrawable(Drawable drawable) {
        this.mNumDrawable = drawable;
        invalidate();
    }

    public Drawable getmBg() {
        return mBg;
    }

    public void setmBg(Drawable mBg) {
        this.mBg = mBg;
        invalidate();
    }
}
