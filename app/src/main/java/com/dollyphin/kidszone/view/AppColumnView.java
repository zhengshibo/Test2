/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.AppInfo;
import com.dollyphin.kidszone.app.AppProvider;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by shibo.zheng on 2016/12/9.
 */
public class AppColumnView extends View {

    private float mAppColumnPaddingButtom;
    private float mAppColumnPaddingLeft;
    private float mAppColumnPaddingTop;

    private Paint mXpaint;
    private Paint mLinePaint;
    private Paint mRect_app_paint;
    private Paint mTitlePaint;
    private Paint appNamePaint;
    private Paint unitApp;
    private Paint unitTime;
    private String[] time = new String[]{"4", "3", "2", "1"};

    private final String SPLIT = "/";
    private AppInfo info;
    private final int TIME_SPACE = 60;
    private final int COLUMNWIDTH = 43;
    private final int SRCWIDTH = 35;
    private final int SRCHEIGHT = 35;
    private String appName;
    private final int MAX_UNIT_LENGTH = 40;
    private final int MAX_APP_LENGTH = 70;

    public AppColumnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppColumnView);
        mAppColumnPaddingButtom = a.getDimension(R.styleable.AppColumnView_appColumnPaddingButtom, 100);
        mAppColumnPaddingLeft = a.getDimension(R.styleable.AppColumnView_appColumnPaddingLeft, 100);
        mAppColumnPaddingTop = a.getDimension(R.styleable.AppColumnView_appColumnPaddingTop, 50);

        mXpaint = new Paint();
        mLinePaint = new Paint();
        mRect_app_paint = new Paint();
        mTitlePaint = new Paint();
        appNamePaint = new Paint();
        unitApp = new Paint();
        unitTime = new Paint();
        a.recycle();
    }

    public void init() {
        mXpaint.setAntiAlias(true);
        mXpaint.setColor(getResources().getColor(R.color.black_333));
        mXpaint.setStrokeWidth(1);
        mXpaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setTextSize(0.5f);
        mLinePaint.setAntiAlias(true);

        mRect_app_paint.setColor(getResources().getColor(R.color.rect_app));
        mRect_app_paint.setStyle(Paint.Style.FILL);
        mRect_app_paint.setAntiAlias(true);

        mTitlePaint.setTextSize(getResources().getDimension(R.dimen.txt_size_28px));
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setAntiAlias(true);

        appNamePaint.setTextSize(getResources().getDimension(R.dimen.txt_size_16px));
        appNamePaint.setColor(getResources().getColor(R.color.black_333));
        appNamePaint.setTextAlign(Paint.Align.CENTER);
        appNamePaint.setAntiAlias(true);

        unitApp.setTextSize(getResources().getDimension(R.dimen.txt_size_24px));
        unitApp.setColor(getResources().getColor(R.color.black_333));
        unitApp.setTextAlign(Paint.Align.LEFT);
        unitApp.setAntiAlias(true);

        unitTime.setTextSize(getResources().getDimension(R.dimen.txt_size_24px));
        unitTime.setColor(getResources().getColor(R.color.black_333));
        unitTime.setTextAlign(Paint.Align.CENTER);
        unitTime.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        init();
        int mWidth = getWidth();
        int mHeight = getHeight();
        String column_time = getResources().getString(R.string.column_time);
        String column_app = getResources().getString(R.string.column_app);
        String top_five_title = getResources().getString(R.string.top_five_title);

        List<AppInfo> appInfos = initData();
        for (int i = 0; i < appInfos.size(); i++) {
            if (appInfos.get(i).getUseTime() == 0) {
                appInfos.remove(i);
                i--;
            }
        }
        appSort(appInfos);

        int leftHeight = (int) (mHeight - mAppColumnPaddingButtom - mAppColumnPaddingTop);
        int hPerHeight = leftHeight / time.length;

        Path yPath = new Path();
        yPath.moveTo(mAppColumnPaddingLeft, mAppColumnPaddingTop - 20);
        yPath.lineTo(mAppColumnPaddingLeft + 7, mAppColumnPaddingTop - 10);
        yPath.lineTo(mAppColumnPaddingLeft - 7, mAppColumnPaddingTop - 10);
        yPath.close();
        canvas.drawPath(yPath, mLinePaint);
        Path xPath = new Path();
        xPath.moveTo(mWidth - mAppColumnPaddingLeft + 10, mHeight - mAppColumnPaddingButtom);
        xPath.lineTo(mWidth - mAppColumnPaddingLeft , mHeight - mAppColumnPaddingButtom - 7);
        xPath.lineTo(mWidth - mAppColumnPaddingLeft , mHeight - mAppColumnPaddingButtom + 7);
        xPath.close();
        canvas.drawPath(xPath, mLinePaint);

        canvas.drawLine(mAppColumnPaddingLeft, mAppColumnPaddingTop - 10, mAppColumnPaddingLeft, mHeight - mAppColumnPaddingButtom, mXpaint);
        canvas.drawLine(mAppColumnPaddingLeft, mHeight - mAppColumnPaddingButtom, mWidth - mAppColumnPaddingLeft, mHeight - mAppColumnPaddingButtom, mXpaint);

        canvas.drawText(column_time, mAppColumnPaddingLeft, (mAppColumnPaddingTop - 40), unitTime);
        canvas.drawText(KidsZoneUtil.catAppName(column_app,MAX_UNIT_LENGTH), (mWidth - mAppColumnPaddingLeft + 12), mHeight - mAppColumnPaddingButtom, unitApp);
        canvas.drawText(top_five_title, mWidth / 2, getResources().getDimension(R.dimen.column_title_martop), mTitlePaint);

        // Y axial
        for (int i = 0; i < time.length; i++) {
            Rect rect = new Rect();
            Paint.FontMetricsInt fontMetricsInt = mTitlePaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetricsInt.bottom - fontMetricsInt.top) / 2;
            int textMarginTop = (int) (i * hPerHeight + mAppColumnPaddingTop + baseline);
            canvas.drawText(time[i], mAppColumnPaddingLeft - 20, textMarginTop, mTitlePaint);

            canvas.drawLine(mAppColumnPaddingLeft, mAppColumnPaddingTop + i * hPerHeight, mWidth - mAppColumnPaddingLeft, mAppColumnPaddingTop + i * hPerHeight, mLinePaint);
        }

        List<AppInfo> subList = null;
        if (appInfos != null) {
            subList = appInfos.size() > 5 ? appInfos.subList(0, 5) : new ArrayList<>(appInfos);
        }
        // X axial
        if (subList != null && subList.size() > 0) {
            int yAxisLength = (int) (mHeight - mAppColumnPaddingTop - mAppColumnPaddingButtom);
            int step = yAxisLength / time.length;
            int xAxisLength = (int) (mWidth - mAppColumnPaddingLeft * 2);
            int childLength = xAxisLength / (subList.size() + 1);
            for (int i = 0; i < subList.size(); i++) {
                Drawable drawable = appInfos.get(i).getIcon();
                int useTime = subList.get(i).getUseTime();
                float f = (float) useTime / TIME_SPACE;
                String format = String.format("%.2f", f);
                int marginLeft = (int) (mAppColumnPaddingLeft + childLength * (i + 1));
                int marginTop = (int) (mHeight - mAppColumnPaddingButtom - useTime * step / TIME_SPACE);
                appName = (String) subList.get(i).getLabel();
                String newAppName = KidsZoneUtil.catAppName(appName,MAX_APP_LENGTH);

                drawable.setBounds(marginLeft - SRCWIDTH / 2, mHeight - 58, marginLeft + SRCHEIGHT / 2, mHeight - 58 + SRCWIDTH);
                drawable.draw(canvas);
                canvas.drawText(newAppName, marginLeft, mHeight - 4, appNamePaint);
                canvas.drawRect(new RectF(marginLeft - COLUMNWIDTH / 2, marginTop, marginLeft + COLUMNWIDTH / 2, mHeight - mAppColumnPaddingButtom), mRect_app_paint);
                canvas.drawText(format, marginLeft, marginTop - 10, mXpaint);
                KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "initDate: useTime==> " + useTime);
            }
        }
    }

    private List<AppInfo> initData() {
        List<AppInfo> list = new ArrayList<>();
        String packageName = "";
        String className = "";
        int count = 0;
        Map<String, String> map = SharePrefereUtils.getOtherAppsUseTime(getContext());
        Set<String> strings = map.keySet();
        HashMap<String, Integer> newMap = new HashMap<>();
        for (String s : strings) {
            String[] split = s.split(SPLIT);
            packageName = split[0];
            className = split[1];
            HashMap<String, Integer> oldmap = KidsZoneUtil.string2Map(map.get(s));
            KidsZoneUtil.initMap(newMap, oldmap);
            Set<String> keySet = newMap.keySet();
            for (String s1 : keySet) {
                count += newMap.get(s1);
            }
            KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "initData: count == " + count + "  packageName == " + packageName + "  className == " + className);
            info = AppProvider.getApp(s);
            info.setUseTime(count);
            list.add(info);
            count = 0;
            newMap.clear();
        }
        return list;
    }

    private List<AppInfo> appSort(final List<AppInfo> list) {
        Collections.sort(list, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo appInfo, AppInfo t1) {
                return t1.getUseTime() - appInfo.getUseTime();
            }
        });
        for (AppInfo appInfo : list) {
            KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "initDate:==>  appName= " + appInfo.getLabel() + "  appInfo useTime== " + appInfo.getUseTime());
        }
        return list;
    }

}
