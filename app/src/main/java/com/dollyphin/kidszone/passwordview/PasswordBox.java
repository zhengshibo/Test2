/*Top Secret*/
package com.dollyphin.kidszone.passwordview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by shibo.zheng on 2016/11/28.
 */
public class PasswordBox extends View {
    public static final int PASSWORD_NUM = 4;
    private Drawable mPasswordBg;
    private int mTextColor;
    private int mDividerColor;
    private float mDividerWidth;
    private float mTextSize;
    private String mPassword = "";
    private onPassWordListener mListener;

    private Paint mPaintBg;
    private Paint mPaintText;

    public PasswordBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        final Resources res = getResources();
        final float defTextSize = res.getDimension(R.dimen.defTextSize);
        final float defDividerWidth = res.getDimension(R.dimen.defDividerWidth);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PasswordBox);
        mPasswordBg = a.getDrawable(R.styleable.PasswordBox_cellBackground);
        mTextColor = a.getColor(R.styleable.PasswordBox_textColor, Color.BLACK);
        mTextSize = a.getDimension(R.styleable.PasswordBox_textSize, defTextSize);
        mDividerColor = a.getColor(R.styleable.PasswordBox_divider_color, Color.TRANSPARENT);
        mDividerWidth = a.getDimension(R.styleable.PasswordBox_divider_width, defDividerWidth);

        mPaintBg = new Paint();
        mPaintText = new Paint();
        a.recycle();
    }

    public void init() {
        mPaintBg.setColor(mDividerColor);
        mPaintBg.setAntiAlias(true);

        mPaintText.setColor(mTextColor);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaintBg);
        canvas.save();

        int cellWidth = (int) ((getMeasuredWidth() - mDividerWidth * (PASSWORD_NUM - 1)) / PASSWORD_NUM);
        int left = 0;
        char[] chars = new char[0];
        if (!TextUtils.isEmpty(mPassword)) {
            chars = mPassword.toCharArray();
        }
        for (int i = 0; i < PASSWORD_NUM; i++) {
            mPasswordBg.setBounds(left, 0, left + cellWidth, getMeasuredHeight());
            mPasswordBg.draw(canvas);
            Rect rect = new Rect();
            if (i < chars.length) {
                mPaintText.getTextBounds(chars, i, 1, rect);
                Drawable drawable = getResources().getDrawable(R.drawable.password);
                Bitmap bitmap = KidsZoneUtil.drawableToBitmap(drawable);
                int textLeft = left + (cellWidth - bitmap.getWidth()) / 2;
                int textTop = (getMeasuredHeight() - bitmap.getHeight()) / 2;
                canvas.drawBitmap(bitmap, textLeft, textTop, mPaintText);
            }
            left += cellWidth + mDividerWidth;
        }
    }

    public void setText(String s) {
        if (s.length() <= PASSWORD_NUM) {
            mPassword = s;
        } else {
            mPassword = s.substring(0, PASSWORD_NUM);
        }
        invalidate();
    }

    public void Backspace() {
        if (TextUtils.isEmpty(mPassword)) {
            return;
        }
        mPassword = mPassword.substring(0, mPassword.length() - 1);
        invalidate();
    }

    public void addText(String s) {
        if (mPassword.length() + 1 <= PASSWORD_NUM) {
            mPassword += s;
            if (mPassword.length() == PASSWORD_NUM) {
                if (mListener != null) {
                    mListener.onComplete(mPassword);
                }
            }
            invalidate();
        }
    }

    public void setTextColor(int TextColor) {
        this.mTextColor = TextColor;
        invalidate();
    }

    public void setPasswordBg(Drawable d) {
        this.mPasswordBg = d;
        invalidate();
    }

    public void setListener(onPassWordListener l) {
        this.mListener = l;
    }

    public interface onPassWordListener {
        void onComplete(String s);
    }
}
