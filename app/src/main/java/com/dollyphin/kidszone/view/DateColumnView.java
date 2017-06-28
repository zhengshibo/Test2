/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by shibo.zheng on 2016/12/13.
 */
public class DateColumnView extends View {

    private Paint mXpaint;
    private Paint mLine;
    private Paint mRecPaint;
    private Paint mTitlePaint;
    private Paint datePaint;
    private Paint unitTime;
    private Paint unitDate;

    private String[] time = new String[]{"4", "3", "2", "1"};
    HashMap<String, Integer> mRecentlyWeekData = new HashMap<>();
    private final int TIME_SPACE = 60;
    private final int COLUMNWIDTH = getContext().getResources().getInteger(R.integer.columnwidth);

    private float mDateColumnPaddingButtom;
    private float mDateColumnPaddingLeft;
    private float mDateColumnPaddingTop;
    private final int MAX_APP_LENGTH = 40;

    public DateColumnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DateColumnView);
        mDateColumnPaddingButtom = a.getDimension(R.styleable.DateColumnView_dateColumnPaddingButtom, 100);
        mDateColumnPaddingLeft = a.getDimension(R.styleable.DateColumnView_dateColumnPaddingLeft, 100);
        mDateColumnPaddingTop = a.getDimension(R.styleable.DateColumnView_dateColumnPaddingTop, 50);

        mXpaint = new Paint();
        mLine = new Paint();
        mRecPaint = new Paint();
        mTitlePaint = new Paint();
        datePaint = new Paint();
        unitTime = new Paint();
        unitDate = new Paint();
    }

    private void init() {
        mXpaint.setAntiAlias(true);
        mXpaint.setColor(getResources().getColor(R.color.black_333));
        mXpaint.setStrokeWidth(1);
        mXpaint.setTextAlign(Paint.Align.CENTER);

        mLine.setColor(Color.GRAY);
        mLine.setStrokeWidth(0.5f);
        mLine.setAntiAlias(true);

        mRecPaint.setStyle(Paint.Style.FILL);
        mRecPaint.setColor(getResources().getColor(R.color.rect_date));
        mRecPaint.setAntiAlias(true);

        mTitlePaint.setTextSize(getResources().getDimension(R.dimen.txt_size_28px));
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setAntiAlias(true);

        datePaint.setTextSize(getResources().getDimension(R.dimen.txt_size_20px));
        datePaint.setColor(getResources().getColor(R.color.black_333));
        datePaint.setTextAlign(Paint.Align.CENTER);
        datePaint.setAntiAlias(true);

        unitTime.setTextSize(getResources().getDimension(R.dimen.txt_size_24px));
        unitTime.setColor(getResources().getColor(R.color.black_333));
        unitTime.setTextAlign(Paint.Align.CENTER);
        unitTime.setAntiAlias(true);

        unitDate.setTextSize(getResources().getDimension(R.dimen.txt_size_24px));
        unitDate.setColor(getResources().getColor(R.color.black_333));
        unitDate.setTextAlign(Paint.Align.LEFT);
        unitDate.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        int mWidth = getWidth();
        int mHeight = getHeight();
        String column_date = getResources().getString(R.string.column_date);
        String use_of_title = getResources().getString(R.string.use_of_day_title);
        String columu_time = getResources().getString(R.string.column_time);

        Map<String, Integer> stringIntegerMap = initDate();
        int leftHeight = (int) (mHeight - mDateColumnPaddingButtom - mDateColumnPaddingTop);
        int hPerHeight = leftHeight / time.length;

        Path yPath = new Path();
        yPath.moveTo(mDateColumnPaddingLeft, mDateColumnPaddingTop - 20);
        yPath.lineTo(mDateColumnPaddingLeft + 7, mDateColumnPaddingTop - 10);
        yPath.lineTo(mDateColumnPaddingLeft - 7, mDateColumnPaddingTop - 10);
        yPath.close();
        canvas.drawPath(yPath, mLine);
        Path xPath = new Path();
        xPath.moveTo(mWidth - mDateColumnPaddingLeft + 10, mHeight - mDateColumnPaddingButtom);
        xPath.lineTo(mWidth - mDateColumnPaddingLeft, mHeight - mDateColumnPaddingButtom - 7);
        xPath.lineTo(mWidth - mDateColumnPaddingLeft, mHeight - mDateColumnPaddingButtom + 7);
        xPath.close();
        canvas.drawPath(xPath, mLine);

        canvas.drawLine(mDateColumnPaddingLeft, mDateColumnPaddingTop - 10, mDateColumnPaddingLeft, mHeight - mDateColumnPaddingButtom, mXpaint);
        canvas.drawLine(mDateColumnPaddingLeft, mHeight - mDateColumnPaddingButtom, mWidth - mDateColumnPaddingLeft, mHeight - mDateColumnPaddingButtom, mXpaint);
        canvas.drawText(columu_time, mDateColumnPaddingLeft, mDateColumnPaddingTop - 40, unitTime);
        canvas.drawText(KidsZoneUtil.catAppName(column_date,MAX_APP_LENGTH), mWidth - mDateColumnPaddingLeft + 12, mHeight - mDateColumnPaddingButtom, unitDate);
        canvas.drawText(use_of_title, mWidth / 2, getResources().getDimension(R.dimen.column_title_martop), mTitlePaint);

        // Y axial
        for (int i = 0; i < time.length; i++) {
            Rect rect = new Rect();
            Paint.FontMetricsInt fontMetricsInt = mTitlePaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetricsInt.bottom - fontMetricsInt.top) / 2;
            int textMarginTop = (int) (i * hPerHeight + mDateColumnPaddingTop + baseline);
            canvas.drawText(time[i], mDateColumnPaddingLeft - 20, textMarginTop, mTitlePaint);
            canvas.drawLine(mDateColumnPaddingLeft, mDateColumnPaddingTop + i * hPerHeight, mWidth - mDateColumnPaddingLeft, mDateColumnPaddingTop + i * hPerHeight, mLine);
        }
        // X axial
        int i = 0;
        if (stringIntegerMap != null && stringIntegerMap.size() > 0) {
            for (Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
                Integer value = entry.getValue();
                float f = (float)value / TIME_SPACE;
                String key = entry.getKey();
                String format = String.format("%.2f", f);
                String substring = key.substring(5, key.length());
                KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "initDate:  key= " + key + "  value= " + value);
                int xAxisLength = (int) (mWidth - mDateColumnPaddingLeft * 2);
                int step = xAxisLength / (stringIntegerMap.size() + 1);
                int marginLeft = (int) (mDateColumnPaddingLeft + step * (i + 1));
                int marginTop = (int) (mHeight - mDateColumnPaddingButtom - value * hPerHeight / TIME_SPACE);
                canvas.drawText(format, marginLeft, marginTop - 10, mXpaint);
                canvas.drawText(substring, marginLeft, mHeight - (mHeight - (mHeight - mDateColumnPaddingButtom)) / 3, datePaint);
                for (int j = 0; j < stringIntegerMap.size(); j++) {
                    canvas.drawRect(new RectF(marginLeft - COLUMNWIDTH / 2, marginTop, marginLeft + COLUMNWIDTH / 2, mHeight - mDateColumnPaddingButtom), mRecPaint);
                }
                i++;
            }
        }
    }

    private Map<String, Integer> initDate() {
        KidsZoneUtil.initMap(mRecentlyWeekData, KidsZoneUtil.string2Map(SharePrefereUtils.getAppUseTime(getContext(), getContext().getPackageName())));
        return keySortMap(mRecentlyWeekData);
    }

    protected static Map<String, Integer> keySortMap(Map<String, Integer> oldMap) {
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
                oldMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> arg0,
                               Map.Entry<String, Integer> arg1) {
                return (int) (strToDate(arg0.getKey()).getTime() - strToDate(arg1.getKey()).getTime());
            }
        });
        Map<String, Integer> newMap = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < list.size(); i++) {
            newMap.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return newMap;
    }

    public static Date strToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

}
