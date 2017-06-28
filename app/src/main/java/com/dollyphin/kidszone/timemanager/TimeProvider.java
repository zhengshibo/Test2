/*Top Secret*/
package com.dollyphin.kidszone.timemanager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dollyphin.kidszone.app.AppInfo;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by hong.wang on 2016/12/4.
 */
public class TimeProvider {
    private final String DATE_DIVISION = "/";
    private final String KEY_VALUE = ":";
    private String mToday;
    private HashMap<String, Integer> mRecentlyWeekData = new HashMap<>();
    private Context mContext;
    private final int ONE_WEEK = 7;

    private AppInfo info;
    private HashMap<String, Integer> mAppData = new HashMap<>();

    public TimeProvider(Context context) {
        mContext = context;
        mToday = KidsZoneUtil.Date2String(new Date(System.currentTimeMillis()));
        KidsZoneUtil.initMap(mRecentlyWeekData, string2Map(SharePrefereUtils.getAppUseTime(context, context.getPackageName())));
    }

    public void updateUseTime(int time) {
        KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "updateUseTime: mToday == " + mToday + " time == " + time);
        saveAppUseTime(time);
    }

    public int getTodayUseTime() {
        KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "addUserTime: mToday == " + mToday + "  mRecentlyWeekData == " + mRecentlyWeekData);
        int time = 0;
        try {
            time = mRecentlyWeekData.get(mToday);
        } catch (Exception e) {
            KidsZoneLog.e(KidsZoneLog.KIDS_TIME_DEBUG, "addUserTime:" + Log.getStackTraceString(e));
        }
        return time;
    }

    public void saveAppUseTime(int time) {
        mRecentlyWeekData.put(mToday, time);
        SharePrefereUtils.setAppUseTime(mContext, mContext.getPackageName(), map2String(mRecentlyWeekData));
    }

    public int startCountOtherAppUseTime(AppInfo info) {
        //save old info
        saveOldInfo(this.info);

        //init new info
        if (info == null) {
            KidsZoneLog.e(KidsZoneLog.KIDS_TIME_DEBUG, "startCountOtherAppUseTime: info is null");
            return -1;
        }
        this.info = info;
        String otherAppUseTime = SharePrefereUtils.getOtherAppUseTime(mContext, KidsZoneUtil.getApp(info.getPackageName(), info.getClassName()));
        KidsZoneUtil.initMap(mAppData, string2Map(otherAppUseTime));
        KidsZoneLog.e(KidsZoneLog.KIDS_TIME_DEBUG, "startCountOtherAppUseTime: mAppData == " + mAppData);
        int time = 0;
        try {
            time = mAppData.get(mToday);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public int getAppUsedTime(AppInfo info) {
        if (info == null) {
            KidsZoneLog.e(KidsZoneLog.KIDS_TIME_DEBUG, "getAppUsedTime: info == " + info);
            return 0;
        }
        String appUseTime = SharePrefereUtils.getOtherAppUseTime(mContext, KidsZoneUtil.getApp(info.getPackageName(), info.getClassName()));
        HashMap<String, Integer> oldMap = KidsZoneUtil.string2Map(appUseTime);
        mAppData.clear();
        KidsZoneUtil.initMap(mAppData, oldMap);
        //add by wanghong for bug 52382 begin
        int time = 0;
        try {
            time = mAppData.get(mToday);
        } catch (Exception e) {
            KidsZoneLog.e(KidsZoneLog.KIDS_TIME_DEBUG, Log.getStackTraceString(e));
        }
        return time;
        //add by wanghong for bug 52382 begin
    }

    public void endCountOtherAppUseTime() {
        this.info = null;
    }

    private void saveOldInfo(AppInfo info) {
        if (info != null) {
            SharePrefereUtils.setOtherAppUseTime(mContext, KidsZoneUtil.getApp(info.getPackageName(), info.getClassName()), map2String(mAppData));
        }
        mAppData.clear();
    }

    public void addOtherAppUseTime(int time) {
        KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "addOtherAppUseTime: info == " + info + "  time == " + time);
        if (info == null) {
            return;
        }
        mAppData.put(mToday, time);
        SharePrefereUtils.setOtherAppUseTime(mContext, KidsZoneUtil.getApp(info.getPackageName(), info.getClassName()), map2String(mAppData));
    }

    private HashMap<String, Integer> string2Map(String data) {
        HashMap<String, Integer> map = null;
        try {
            if (TextUtils.isEmpty(data)) {
                KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "string2Map: data is null");
                return null;
            }
            map = new HashMap<>();
            String[] dayData = TextUtils.split(data, DATE_DIVISION);
            int length = dayData.length;
            KidsZoneLog.i(KidsZoneLog.KIDS_TIME_DEBUG, "string2Map: dayData.length ==> " + length);
            for (int i = 0; i < length; i++) {
                String[] strings = TextUtils.split(dayData[i], KEY_VALUE);
                map.put(strings[0], Integer.valueOf(strings[1]));
            }

        } catch (NumberFormatException e) {
            KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "string2Map:" + Log.getStackTraceString(e));
        } catch (Exception e) {
            KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "string2Map:" + Log.getStackTraceString(e));
        }
        return map;
    }

    private String map2String(HashMap<String, Integer> map) {
        if (null == map) {
            KidsZoneLog.e(KidsZoneLog.KIDS_TIME_DEBUG, "map2String: map is null");
        }
        Set<String> strings = map.keySet();
        StringBuilder builder = new StringBuilder();
        for (String s : strings) {
            builder.append(s).append(KEY_VALUE).append(map.get(s)).append(DATE_DIVISION);
        }
        KidsZoneLog.e(KidsZoneLog.KIDS_TIME_DEBUG, "map2String: string is  == " + builder.toString().substring(0, builder.toString().length() - 1));
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    public void setToday(Date date) {
        mToday = KidsZoneUtil.Date2String(date);
    }
}
