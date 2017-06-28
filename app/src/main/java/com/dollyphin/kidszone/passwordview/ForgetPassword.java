/*Top Secret*/
package com.dollyphin.kidszone.passwordview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dollyphin.kidszone.R;

/**
 * Created by shibo.zheng on 2016/12/1.
 */
public class ForgetPassword extends View {
    private final int ANSWER_LENGHT = 2;
    private String answer = "";
    private Paint mPaintRect;
    private Paint mPaintText;
    private int mForDividerColor;
    private int mForget_textColor;
    private static int CODE[] = new int[]{R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5,
            R.drawable.icon6, R.drawable.icon7, R.drawable.icon8, R.drawable.icon9, R.drawable.icon10};
    private static String CODE_VALUE[] = new String[]{"10", "12", "14", "11", "14", "18", "21", "5", "5", "11"};

    public int mIndex = 0;

    public int getCodeIndex() {
        return mIndex = (int) (Math.random() * CODE.length);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public Drawable getCode(int index) {
        return getResources().getDrawable(CODE[index]);
    }

    public String getCodeValue(int index) {
        return CODE_VALUE[index];
    }

    public boolean isOkAnswer() {
        return getCodeValue(mIndex).equals(answer);
    }

    public ForgetPassword(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForgetPassword);
        mForDividerColor = a.getColor(R.styleable.ForgetPassword_forget_divider_color, Color.WHITE);
        mForget_textColor = a.getColor(R.styleable.ForgetPassword_forget_textColor, Color.BLACK);

        mPaintRect = new Paint();
        mPaintText = new Paint();
    }

    private void init() {
        int textsize = getContext().getResources().getInteger(R.integer.answer_textsize);
        mPaintRect.setColor(mForDividerColor);
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setStrokeWidth(5);
        mPaintRect.setAntiAlias(true);

        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(textsize);
        mPaintText.setFakeBoldText(getResources().getBoolean(R.bool.answer_bold));
        mPaintText.setColor(mForget_textColor);
        mPaintText.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        int measuredWidth = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();
        int measuredHeight = getMeasuredHeight() - getPaddingBottom() - getPaddingTop();
        int answerHeightpis = getContext().getResources().getInteger(R.integer.answerheightpis);
        int answerHeight = getContext().getResources().getInteger(R.integer.answerheight);
        int left = 0;
        char[] chars = new char[0];
        if (!TextUtils.isEmpty(answer)) {
            chars = answer.toCharArray();
        }
        for (int i = 0; i < ANSWER_LENGHT; i++) {
            if (i < chars.length) {
                if (chars.length == 1) {
                    int textLeft = left + measuredWidth / 2;
                    int textTop = measuredHeight / answerHeightpis * answerHeight;
                    canvas.drawText(chars, i, 1, textLeft, textTop, mPaintText);
                } else if (chars.length == 2) {
                    int textLeft = left + measuredWidth / 3;
                    int textTop = measuredHeight / answerHeightpis * answerHeight;
                    canvas.drawText(chars, i, 1, textLeft, textTop, mPaintText);
                }
            }
            left += measuredWidth / 3;
        }
    }

    public void setText(String s) {
        if (s.length() <= ANSWER_LENGHT) {
            answer = s;
        } else {
            answer = s.substring(0, ANSWER_LENGHT);
        }
        invalidate();
    }

    public void addText(String s) {
        if (answer.length() + 1 <= ANSWER_LENGHT) {
            answer += s;
            invalidate();
        }
    }
}
