/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;

/**
 * Created by hong.wang on 2017/1/10.
 */

public class UnlockBar extends View {
    private Drawable mThumb;
    private Drawable mProgressdrawable;
    private int mProgress;
    private int mMax;
    private int mOneProgress = 1;
    private int mX = -1;
    private OnUnlockBarChangeListener mlistener;
    private int mThumbLeft = 0;
    private int mMaxThumbMove;
    private int mMinThumbMove;


    public UnlockBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UnlockBar);
        mProgress = typedArray.getInteger(R.styleable.UnlockBar_progress, 0);
        mMax = typedArray.getInteger(R.styleable.UnlockBar_max, 0) - 1;
        mThumb = typedArray.getDrawable(R.styleable.UnlockBar_thumb);
        mProgressdrawable = typedArray.getDrawable(R.styleable.UnlockBar_progressdrawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 0;
        int desiredHeight = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (mProgressdrawable != null) {
            mMaxThumbMove = mProgressdrawable.getIntrinsicWidth() + 50;
            int backgroundWidth = mProgressdrawable.getIntrinsicWidth();
            int backgroundHeight = mProgressdrawable.getIntrinsicHeight();
            desiredHeight = Math.max(backgroundHeight, desiredHeight);
            desiredWidth = Math.max(backgroundWidth, desiredWidth);
        }

        if (mThumb != null) {
            int mThumbHeight = mThumb.getIntrinsicHeight();
            int mThumbWidth = mThumb.getIntrinsicWidth();
            desiredHeight = Math.max(mThumbHeight, desiredHeight);
            desiredWidth += mThumbWidth * 2;
            mMinThumbMove = mThumbLeft = mThumbWidth - 20;
        }


        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }
        mOneProgress = (mMaxThumbMove - mMinThumbMove) / mMax;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int ViewWidth = getWidth() - getPaddingStart() - getPaddingEnd();
        int ViewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (mProgressdrawable != null) {
            int width = mProgressdrawable.getIntrinsicWidth();
            int height = mProgressdrawable.getIntrinsicHeight();
            int left = (ViewWidth - width) / 2;
            int top = (ViewHeight - height) / 2;
            mProgressdrawable.setBounds(left, top, left + width, top + height);
            mProgressdrawable.draw(canvas);
            KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onDraw: width == " + width + "   height == " + height);
        }
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onDraw: ViewWidth == " + ViewWidth + "   ViewHeight == " + ViewHeight);
        if (mThumb != null) {
            KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onDraw:  === 000 ===");
            int width = mThumb.getIntrinsicWidth();
            int height = mThumb.getIntrinsicHeight();
            int top = 0;
            mThumb.setBounds(mThumbLeft, top, mThumbLeft + width, top + height);
            mThumb.draw(canvas);
        }

    }

    public void setOnUnlockBarChangeListener(OnUnlockBarChangeListener listener) {
        this.mlistener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                KidsZoneLog.d(true, "dispatchTouchEvent: down x == " + x + "  y == " + y);
                if (mThumb != null && (mThumb.getBounds().left < x && x < mThumb.getBounds().right)) {
                    KidsZoneLog.d(true, "dispatchTouchEvent: down  =========== 00  ============");
                    mX = x;
                    mThumbLeft = mMinThumbMove;
                    if (mlistener != null) mlistener.onStartTrackingTouch(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                KidsZoneLog.d(true, "dispatchTouchEvent: MOVE mX == " + mX);
                if (mX != -1) {
                    KidsZoneLog.d(true, "dispatchTouchEvent: MOVE  =========== 11  ============");
                    mThumbLeft = (int) (event.getX() - mX) + mMinThumbMove;
                    if (mThumbLeft < mMinThumbMove) {
                        mThumbLeft = mMinThumbMove;
                    }

                    if (mThumbLeft > mMaxThumbMove) {
                        mThumbLeft = mMaxThumbMove;
                    }
                    if (mlistener != null)
                        mlistener.onProgressChanged(this, mProgress = (mThumbLeft - mMinThumbMove) / mOneProgress);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                mX = -1;
                if (mlistener != null) mlistener.onStopTrackingTouch(this);
                break;
        }
        return true;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getMax() {
        return mMax;
    }

    public void toMinProgress() {
        mThumbLeft = mMinThumbMove;
        mProgress = 0;
        invalidate();
    }

    public interface OnUnlockBarChangeListener {
        void onStartTrackingTouch(UnlockBar unlockBar);

        void onStopTrackingTouch(UnlockBar unlockBar);

        void onProgressChanged(UnlockBar unlockBar, int progress);
    }
}
