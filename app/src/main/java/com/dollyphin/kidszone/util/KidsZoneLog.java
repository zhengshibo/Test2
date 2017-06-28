/*Top Secret*/
package com.dollyphin.kidszone.util;

import android.util.Log;

/**
 * Created by feng.shen on 2016/11/23.
 */

public class KidsZoneLog {
    public static final boolean KIDS_MAIN_DEBUG = true;
    public static final boolean KIDS_BOOT_DEBUG = false;
    public static final boolean KIDS_CONTROL_DEBUG = true;
    public static final boolean KIDS_TIME_DEBUG = true;
    public static final boolean KIDS_LOCK_DEBUG = true;
    public static final boolean KIDS_WALLPAPER_DEBUG = false;
    public static final boolean KIDS_SYSTEMUI_DEBUG = false;
    public static final boolean KIDS_APP_DEBUG = false;
    public static final boolean KIDS_UTILS_DEBUG = false;
    public static final boolean KIDS_CRASH_DEBUG = false;
    public static final boolean KIDS_GUIDE_DEBUG = false;
    public static final boolean KIDS_COLUMN_DEBUG = false;
    public static final boolean KIDS_PERMISSION_DEBUG = false;
    public static final boolean KIDS_LANGUAGE_DEBUG = false;
    public static final boolean KIDS_THEME_DEBUG = false;

    private static final String TAG = "KidsZone";

    public static boolean isLoggable(boolean debug) {
        return debug || Log.isLoggable(TAG, Log.VERBOSE);
    }

    public static void v(boolean debug, String message) {
        if (isLoggable(debug)) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.v(tag, message);
        }
    }

    public static void d(boolean debug, String message) {
        if (isLoggable(debug)) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.d(tag, message);
        }
    }

    public static void i(boolean debug, String message) {
        if (isLoggable(debug)) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.i(tag, message);
        }
    }

    public static void w(boolean debug, String message) {
        if (isLoggable(debug)) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.w(tag, message);
        }
    }

    public static void e(boolean debug, String message) {
        if (isLoggable(debug)) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.e(tag, message);
        }
    }

    public static String getDefaultTag(StackTraceElement stackTraceElement) {
        String fileName = stackTraceElement.getFileName();
        String stringArray[] = fileName.split("\\.");
        String tag = stringArray[0];
        return tag;
    }

}
