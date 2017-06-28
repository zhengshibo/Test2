/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.Context;

import com.dollyphin.kidszone.util.KidsZoneUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class AppProvider {
    public static ArrayList<AppInfo> data = new ArrayList<>();
    public static ArrayList<AppInfo> display = new ArrayList<>();

    public static void clear() {
        data.clear();
        display.clear();
    }

    public static int size() {
        return data.size();
    }

    public static void updateAllApp(AppInfo info) {
        data.add(info);
    }

    public static void updateDisplayApp(AppInfo info) {
        display.add(info);
    }

    public static void remove(AppInfo info) {
        int index = display.indexOf(info);
        if (index != -1) {
            display.remove(index);
        }
    }

    public static AppInfo getApp(String app) {
        String[] name = KidsZoneUtil.spiltApp(app);
        AppInfo info = new AppInfo(name[0], name[1]);
        int index = data.indexOf(info);
        if (index != -1) {
            return data.get(index);
        }
        return null;
    }

    public static void sort(Context context) {
        Locale curLocale = context.getResources().getConfiguration().locale;
        String local = curLocale.getLanguage();
        boolean localeRequiresSectionSorting = local.endsWith("zh");
        if (localeRequiresSectionSorting) {
            KidsZoneUtil.sortAppForAlphaChina(display);
        } else {
            KidsZoneUtil.sortAppForAlpha(display);
        }

    }

    public static void sortAll(Context context) {
        Locale curLocale = context.getResources().getConfiguration().locale;
        String local = curLocale.getLanguage();
        boolean localeRequiresSectionSorting = local.endsWith("zh");
        if (localeRequiresSectionSorting) {
            KidsZoneUtil.sortAppForAlphaChina(data);
        } else {
            KidsZoneUtil.sortAppForAlpha(data);
        }

    }

    public static int getIndex(int index) {
        int start = 0;
        int end = display.size() - 1;

        while (start <= end) {
            int point = (start + end) / 2;
            int cIndex = display.get(point).getIndex();
            if (index > cIndex) {
                start = point + 1;
            } else if (index < cIndex) {
                end = point - 1;
            }
        }
        return start;
    }
}
