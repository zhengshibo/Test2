/*Top Secret*/
package com.dollyphin.kidszone.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dollyphin.kidszone.view.LimitTimeView;

import java.util.Map;

/**
 * Created by feng.shen on 2016/11/23.
 */

public class SharePrefereUtils {
    private static final String DISPLAY_APP_LIST_PREF = "display_app_pref";
    private static final String PERSON_INFO = "person.pref";
    public static final String SETTING_PARAM = "setting_param.pref";
    private static final String THIS_USE_TIME = "this_use_time.pref";
    private static final String OTHER_USE_TIME = "other_use_time.pref";
    private static final String PASSWORD = "password.pref";

    private static final String FIRST_START_PREF = "first_start_pref";
    private static final String FIRST_START_KEY = "first_start_key";

    private static final String DISPLAY_APP_NUM = "display_app_num";

    private static final String DEVICE_PROFILE_PREF = "device_profile.pref";
    private static final String CURRENT_PAGE_KEY = "current_page_key";
    private static final String CURRENT_LANGUAGE_KEY = "current_language_key";

    private static final String ALLAPPS_LIST_PREF = "allapps_list.pref";

    private static final String ENTER_PARENT_MODE_PREF = "enter_parent_mode.pref";
    private static final String ENTER_PARENT_MODE = "enter_parent_mode";

    public static final int INVALID_INT = -1;

    public abstract class PASSWORD_ITEM {
        public static final String PASSWORD = "pwd";
    }


    private static class PERSON_INFO_PREF {
        static final String DEFAULT_STRING = "";
        static final String NAME = "name";
        static final String BIRTHDAY = "birthday";
        static final String HEADER_IMG = "header_img";
    }

    public static class SETTING_PARAM_PREF {
        public static final String USE_TIME = "use_time";
        public static final String BREAK_TIME = "break_time";
        public static final String IS_OPEN_DATA = "is_open_data";
        public static final String SYSTEM_IS_OPEN_DATA = "system_is_open_data";
        public static final String IS_EYE_MODEL = "is_eye_model";
        public static final String SYSTEM_IS_EYE_MODEL = "system_is_eye_model";
        public static final String SYSTEM_BRIGHTNESS = "system_brightness";
        public static final String SYSTEM_BRIGHTNESS_MODE = "system_brightness_mode";
        public static final String KIDS_ZONE_BRIGHTNESS = "kids_zone_brightness";
        public static final String SYSTEM_VOLUME = "system_volume";
        public static final String KIDS_ZONE_VOLUME = "kids_zone_volume";
        public static final String WAPPAPER = "wallppaper";
        public static final String WIFI = "wifi";
        private static final int DEFAULT_BRIGHTNESS = 200;
        private static final int DEFAULT_VOLUME = 10;
        public static final String SYSTEM_LOCK_SCREEN_STATE = "system_lock_screen_state";
    }

    public static void setEnterParentMode(Context context, boolean flag) {
        SharedPreferences preferences = context.getSharedPreferences(ENTER_PARENT_MODE_PREF, Context.MODE_PRIVATE);
        Editor edit = preferences.edit();
        edit.putBoolean(ENTER_PARENT_MODE, flag);
        edit.commit();
    }

    public static boolean isEnterParentMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ENTER_PARENT_MODE_PREF, Context.MODE_PRIVATE);
        return preferences.getBoolean(ENTER_PARENT_MODE, false);
    }


    public static int getDislayAppNum(Context context) {
        return context.getSharedPreferences(DISPLAY_APP_LIST_PREF, Context.MODE_PRIVATE)
                .getInt(DISPLAY_APP_NUM, 0);
    }

    public static void setDislayAppNum(Context context, int size) {
        SharedPreferences mAllAppPref = context.getSharedPreferences(DISPLAY_APP_LIST_PREF, Context.MODE_PRIVATE);
        Editor editor = mAllAppPref.edit();
        editor.putInt(DISPLAY_APP_NUM, size);
        editor.commit();
    }

    public static void clearDislayAppNum(Context context) {
        SharedPreferences mAllAppPref = context.getSharedPreferences(DISPLAY_APP_LIST_PREF, Context.MODE_PRIVATE);
        Editor editor = mAllAppPref.edit();
        editor.clear();
        editor.commit();
    }

    public static boolean isDislpay(Context context, String app) {
        SharedPreferences mAllAppPref = context.getSharedPreferences(DISPLAY_APP_LIST_PREF, Context.MODE_PRIVATE);
        return mAllAppPref.getBoolean(app, false);
    }

    public static void updateDisplayApp(Context context, String app, boolean display) {
        SharedPreferences mAllAppPref = context.getSharedPreferences(DISPLAY_APP_LIST_PREF, Context.MODE_PRIVATE);
        Editor editor = mAllAppPref.edit();
        if (display) {
            editor.putBoolean(app, display);
        } else {
            editor.remove(app);
        }
        editor.commit();
    }

    public static void saveUserName(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(PERSON_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(PERSON_INFO_PREF.NAME, name);
        edit.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PERSON_INFO, Context.MODE_PRIVATE);
        return preferences.getString(PERSON_INFO_PREF.NAME, PERSON_INFO_PREF.DEFAULT_STRING);
    }

    public static void saveUserBirthday(Context context, String birthday) {
        SharedPreferences preferences = context.getSharedPreferences(PERSON_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(PERSON_INFO_PREF.BIRTHDAY, birthday);
        edit.commit();
    }

    public static String getUserBirthday(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PERSON_INFO, Context.MODE_PRIVATE);
        return preferences.getString(PERSON_INFO_PREF.BIRTHDAY, PERSON_INFO_PREF.DEFAULT_STRING);
    }

    public static void saveUserHeaderImg(Context context, String path) {
        SharedPreferences preferences = context.getSharedPreferences(PERSON_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(PERSON_INFO_PREF.NAME, path);
        edit.commit();
    }

    public static String getUserHeaderImg(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PERSON_INFO, Context.MODE_PRIVATE);
        return preferences.getString(PERSON_INFO_PREF.HEADER_IMG, PERSON_INFO_PREF.DEFAULT_STRING);
    }

    public static void saveUser(Context context, String name, String birthday, String path) {
        saveUserName(context, name);
        saveUserBirthday(context, birthday);
        saveUserHeaderImg(context, path);
    }

    public static boolean getBooleanParam(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SETTING_PARAM, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void setBooleanParam(Context context, String key, boolean isCheck) {
        SharedPreferences preferences = context.getSharedPreferences(SETTING_PARAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, isCheck);
        edit.commit();
    }

    public static void setIntParam(Context context, String key, int num) {
        SharedPreferences preferences = context.getSharedPreferences(SETTING_PARAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key, num);
        edit.commit();
    }

    public static int getIntParam(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SETTING_PARAM, Context.MODE_PRIVATE);
        return preferences.getInt(key, INVALID_INT);
    }

    public static void saveUseTime(Context context, int time) {
        setIntParam(context, SETTING_PARAM_PREF.USE_TIME, time);
    }

    public static void saveBreakTime(Context context, int time) {
        setIntParam(context, SETTING_PARAM_PREF.BREAK_TIME, time);
    }

    public static int getUseTime(Context context) {
        int param = getIntParam(context, SETTING_PARAM_PREF.USE_TIME);
        if (param == INVALID_INT) {
            param = LimitTimeView.DEFAULT_USE_TIME;
        }
        return param;
    }

    public static int getBreakTime(Context context) {
        int param = getIntParam(context, SETTING_PARAM_PREF.BREAK_TIME);
        if (param == INVALID_INT) {
            param = LimitTimeView.DEFAULT_BREAK_TIME;
        }
        return param;
    }

    public static boolean isFirstStart(Context context) {
        SharedPreferences firstStartPref = context.getSharedPreferences(FIRST_START_PREF, Context.MODE_PRIVATE);
        return firstStartPref.getBoolean(FIRST_START_KEY, true);
    }

    public static void setNotFirstStart(Context context) {
        SharedPreferences firstStartPref = context.getSharedPreferences(FIRST_START_PREF, Context.MODE_PRIVATE);
        Editor editor = firstStartPref.edit();
        editor.putBoolean(FIRST_START_KEY, false);
        editor.commit();
    }

    public static void setAppUseTime(Context context, String key, String time) {
        SharedPreferences preferences = context.getSharedPreferences(THIS_USE_TIME, Context.MODE_PRIVATE);
        Editor edit = preferences.edit();
        edit.putString(key, time);
        edit.commit();
    }

    public static String getAppUseTime(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(THIS_USE_TIME, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void setOtherAppUseTime(Context context, String key, String time) {
        SharedPreferences preferences = context.getSharedPreferences(OTHER_USE_TIME, Context.MODE_PRIVATE);
        Editor edit = preferences.edit();
        edit.putString(key, time);
        edit.commit();
    }

    public static String getOtherAppUseTime(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(OTHER_USE_TIME, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static String getPassword(Context context) {
        SharedPreferences passwordPreferences = context.getSharedPreferences(PASSWORD, Context.MODE_PRIVATE);
        return passwordPreferences.getString(PASSWORD_ITEM.PASSWORD, "0000");
    }

    public static boolean setPassword(Context context, String pwd) {
        SharedPreferences passwordPreferences = context.getSharedPreferences(PASSWORD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = passwordPreferences.edit();
        editor.putString(PASSWORD_ITEM.PASSWORD, pwd);
        return editor.commit();
    }

    public static void setWallpaper(Context context, int position) {
        setIntParam(context, SETTING_PARAM_PREF.WAPPAPER, position);
    }

    public static int getWallpaper(Context context) {
        int wallPaper = getIntParam(context, SETTING_PARAM_PREF.WAPPAPER);
        if (wallPaper == INVALID_INT) {
            wallPaper = 0;
        }
        return wallPaper;
    }

    public static int getKidsZoneBrightness(Context context) {
        int brightness = getIntParam(context, SETTING_PARAM_PREF.KIDS_ZONE_BRIGHTNESS);
        if (brightness == INVALID_INT) {
            brightness = SETTING_PARAM_PREF.DEFAULT_BRIGHTNESS;
        }
        return brightness;
    }

    public static int getSystemBrightness(Context context) {
        return getIntParam(context, SETTING_PARAM_PREF.SYSTEM_BRIGHTNESS);
    }

    public static void setSystemBrightness(Context context, int brightness) {
        setIntParam(context, SETTING_PARAM_PREF.SYSTEM_BRIGHTNESS, brightness);
    }

    public static void setKidsZoneBrightness(Context context, int brightness) {
        setIntParam(context, SETTING_PARAM_PREF.KIDS_ZONE_BRIGHTNESS, brightness);
    }

    public static void setSystemBrightnessMode(Context context, int mode) {
        setIntParam(context, SETTING_PARAM_PREF.SYSTEM_BRIGHTNESS_MODE, mode);
    }

    public static int getSystemBrightnessMode(Context context) {
        return getIntParam(context, SETTING_PARAM_PREF.SYSTEM_BRIGHTNESS_MODE);
    }

    public static int getSystemVolume(Context context) {
        return getIntParam(context, SETTING_PARAM_PREF.SYSTEM_VOLUME);
    }

    public static void setKidsZoneVolume(Context context, int volume) {
        setIntParam(context, SETTING_PARAM_PREF.KIDS_ZONE_VOLUME, volume);
    }

    public static void setSystemVolume(Context context, int volume) {
        setIntParam(context, SETTING_PARAM_PREF.SYSTEM_VOLUME, volume);
    }

    public static int getKidsZoneVolume(Context context) {
        int volume = getIntParam(context, SETTING_PARAM_PREF.KIDS_ZONE_VOLUME);
        if (volume == INVALID_INT) {
            volume = SETTING_PARAM_PREF.DEFAULT_VOLUME;
        }
        return volume;
    }

    public static void setKidsZoneEyeMode(Context context, int mode) {
        setIntParam(context, SETTING_PARAM_PREF.IS_EYE_MODEL, mode);
    }

    public static void setSystemEyeMode(Context context, int mode) {
        setIntParam(context, SETTING_PARAM_PREF.SYSTEM_IS_EYE_MODEL, mode);
    }

    public static boolean isKidsZoneEyeMode(Context context) {
        return (KidsZoneUtil.EYE_MODE == getIntParam(context, SETTING_PARAM_PREF.IS_EYE_MODEL));
    }

    public static boolean isSystemEyeMode(Context context) {
        return (KidsZoneUtil.EYE_MODE == getIntParam(context, SETTING_PARAM_PREF.SYSTEM_IS_EYE_MODEL));
    }

    public static Map<String, String> getOtherAppsUseTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(OTHER_USE_TIME, Context.MODE_PRIVATE);
        Map<String, String> all = (Map<String, String>) preferences.getAll();
        return all;
    }

    public static void setSystemLockScreenState(Context context, boolean isdisable) {
        setBooleanParam(context, SETTING_PARAM_PREF.SYSTEM_LOCK_SCREEN_STATE, isdisable);
    }

    public static boolean getSystemLockScreenState(Context context) {
        return getBooleanParam(context, SETTING_PARAM_PREF.SYSTEM_LOCK_SCREEN_STATE);
    }

    public static void saveCurrentPage(Context context, int page) {
        SharedPreferences currentPref = context.getSharedPreferences(DEVICE_PROFILE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentPref.edit();
        editor.putInt(CURRENT_PAGE_KEY, page);
        editor.commit();
    }

    public static int getCurrentPage(Context context) {
        SharedPreferences currentPref = context.getSharedPreferences(DEVICE_PROFILE_PREF, Context.MODE_PRIVATE);
        return currentPref.getInt(CURRENT_PAGE_KEY, 0);
    }

    public static void addAllAppsSort(Context context, String appname, int index) {
        SharedPreferences allappsPref = context.getSharedPreferences(ALLAPPS_LIST_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = allappsPref.edit();
        editor.putInt(appname, index);
        editor.commit();
    }

    public static void clearAllAppsSort(Context context) {
        SharedPreferences allappsPref = context.getSharedPreferences(ALLAPPS_LIST_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = allappsPref.edit();
        editor.clear();
        editor.commit();
    }

    public static int getAllAppsIndex(Context context, String appname) {
        SharedPreferences allappsPref = context.getSharedPreferences(ALLAPPS_LIST_PREF, Context.MODE_PRIVATE);
        return allappsPref.getInt(appname, -1);
    }

    public static void removeAllAppsIndex(Context context, String appname) {
        SharedPreferences allappsPref = context.getSharedPreferences(ALLAPPS_LIST_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = allappsPref.edit();
        editor.remove(appname);
        editor.commit();
    }

    public static void setLocaleLanguage(Context context, String language) {
        SharedPreferences currentPref = context.getSharedPreferences(DEVICE_PROFILE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentPref.edit();
        editor.putString(CURRENT_LANGUAGE_KEY, language);
        editor.commit();
    }

    public static String getLanguage(Context context) {
        SharedPreferences currentPref = context.getSharedPreferences(DEVICE_PROFILE_PREF, Context.MODE_PRIVATE);
        return currentPref.getString(CURRENT_LANGUAGE_KEY, "none");
    }
}
