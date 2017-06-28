/*Top Secret*/
package com.dollyphin.kidszone.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.AppInfo;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.dollyphin.kidszone.util.KidsZoneLog.KIDS_MAIN_DEBUG;
import static com.dollyphin.kidszone.util.KidsZoneLog.KIDS_UTILS_DEBUG;

/**
 * Created by feng.shen on 2016/11/23.
 */

public class KidsZoneUtil {
    public static final String KIDS_CRASH_FILE = "/KidsZone/crashs/";
    public static final String KIDS_CRASH_SUFFIX = ".log";
    public static final String KIDS_CRASH_TXT_SUFFIX = ".txt";

    public static final String APP_FILTER_FOR_NAME = "/";
    public static final String APP_FILTER_FOR_PACKAGE = ":";
    public static final String HEAD_IMG_PATH = "/KidsZone/icon_user/";
    public static final String HEAD_PHOTOS_PATH = "/KidsZone/icon_photo/";
    public static final String HEAD_IMG_NAME = "myicon.jpg";
    public static final String IMAGE_FILE_NAME = "user_image.jpg";
    //setting
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String ID_STRING_TYPE = "string";
    private static final String ID_DRAWABLE_TYPE = "drawable";
    private static final String WALLPAPER_NAME = "wallpaper_";

    //eye mode
    public static final String EYE_PROTECTION_MODE = "eye_protection_mode";
    public static final int EYE_MODE = 1;

    private static final int ONE_WEEK = 7;
    private static final String DATE_DIVISION = "/";
    private static final String KEY_VALUE = ":";

    private static final String STATUS_BAR_SERVICE = "statusbar";
    private static final String CLASS_STATUS_BAR_MANAGER = "android.app.StatusBarManager";
    private static final String METHOD_DISABLE = "disable";

    public static final String DISABLE_NONE = "disable_none";
    public static final String DISABLE_RECNETS = "disable_recnets";
    public static final String DISABLE_NAV = "disable_nav";
    public static final String DISABLE_PHONE = "disable_phone";

    public static final int PER_USER_RANGE = 100000;//add by wanghong for bug 51908

    public static final String CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED = "camera_double_tap_power_gesture_disabled";

    private static String mSystem = "System";
    private static String mGlobal = "Global";

    static String inputMethod;

    private static final Rect sOldBounds = new Rect();
    private static final Canvas sCanvas = new Canvas();

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                Paint.FILTER_BITMAP_FLAG));
    }


    public static int getStatusBatHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        KidsZoneLog.d(KIDS_UTILS_DEBUG, "==getStatusBatHeight==>" + result);
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        KidsZoneLog.d(KIDS_UTILS_DEBUG, "==getNavigationBarHeight==>" + result);
        return result;
    }

    public static void hideInputMethod(Context context, View v) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void ShowInputMethod(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public static int getRealScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels + getNavigationBarHeight(context);
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getRealHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels + getNavigationBarHeight(context);
    }

    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public static String getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionCode + "";
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getNumOfPage(Context context) {
        return context.getResources().getInteger(R.integer.config_KidsZoneCellXCount) *
                context.getResources().getInteger(R.integer.config_KidsZoneCellYCount);
    }

    public static Integer getCellX(Context context) {
        return context.getResources().getInteger(R.integer.config_KidsZoneCellXCount);
    }

    public static Integer getCellY(Context context) {
        return context.getResources().getInteger(R.integer.config_KidsZoneCellYCount);
    }

    public static Date StringToDateFormat(String date, String parttern) {
        Date time = null;
        if (TextUtils.isEmpty(date)) {
            KidsZoneLog.e(KidsZoneLog.KIDS_UTILS_DEBUG, "StringToDateFormat:date is \"\" or null");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(parttern);
        try {
            time = sdf.parse(date);
        } catch (ParseException e) {
            KidsZoneLog.e(KidsZoneLog.KIDS_SYSTEMUI_DEBUG, android.util.Log.getStackTraceString(e));
        } catch (Exception e) {
            KidsZoneLog.e(KidsZoneLog.KIDS_SYSTEMUI_DEBUG, android.util.Log.getStackTraceString(e));
        }
        return time;
    }

    public static String Date2String(Date date) {
        if (null == date) {
            KidsZoneLog.e(KidsZoneLog.KIDS_UTILS_DEBUG, "Date2String: date is null");
            return "";
        }
        return new SimpleDateFormat(KidsZoneUtil.DATE_FORMAT, Locale.US).format(date);//modify by wanghong for bug 51909 51907
    }

    /**
     * Get data from date  (ps:year,month,day)
     *
     * @param date
     * @param dateType
     * @return
     */
    private static int getInteger(Date date, int dateType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(dateType);
    }

    public static int getYear(Date date) {
        return getInteger(date, Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        return getInteger(date, Calendar.MONTH);
    }

    public static int getDay(Date date) {
        return getInteger(date, Calendar.DATE);
    }

    public static void saveFile(Bitmap bm) throws IOException {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + HEAD_IMG_PATH;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File myIconFile = new File(path + HEAD_IMG_NAME);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myIconFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    public static Bitmap getHeadImg() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + HEAD_IMG_PATH + HEAD_IMG_NAME;
        if (path == null) {
            return null;
        }
        if (!new File(path).exists()) return null;
        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inPreferredConfig = Bitmap.Config.ALPHA_8;
        ops.inPurgeable = true;
        ops.inInputShareable = true;
        return BitmapFactory.decodeFile(path, ops);
    }

    public static Bitmap createBitmap(Context context, Bitmap bitmap) {
        int srcWidth = context.getResources().getDimensionPixelOffset(R.dimen.photos_user_size);
        int srcHeight = context.getResources().getDimensionPixelOffset(R.dimen.photos_user_size);

        int frameWidth = context.getResources().getDimensionPixelOffset(R.dimen.frame_user_size);
        int frameHeight = context.getResources().getDimensionPixelOffset(R.dimen.frame_user_size);

        int top, left;
        int color = 0xff424242;
        int round = srcWidth / 2;
        left = top = (frameWidth - srcWidth) / 2;

        Rect distRect = new Rect(left, top, srcWidth + left, srcHeight + top);
        Rect srcRect = new Rect(0, 0, srcWidth, srcHeight);
        RectF rectF = new RectF(distRect);

        if (bitmap == null) {
            return null;
        }
        Bitmap icon = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(icon);
        Matrix matrix = new Matrix();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, round, round, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) srcWidth) / width;
        float scaleHeight = ((float) srcHeight) / height;

        matrix.postScale(scaleWidth, scaleHeight);

        if (width < 0 || height < 0) {
            return null;
        }

        Bitmap srcIcon = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        canvas.drawBitmap(srcIcon, srcRect, distRect, paint);

        if (srcIcon != null && !srcIcon.isRecycled()) {
            srcIcon.recycle();
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return icon;
    }

    public static Bitmap createIcon(Context context, Drawable icon) {
        synchronized (sCanvas) {
            int width = icon.getIntrinsicWidth();
            int height = icon.getIntrinsicHeight();

            int sourceWidth = icon.getIntrinsicWidth();
            int sourceHeight = icon.getIntrinsicHeight();
            if (sourceWidth > 0 && sourceHeight > 0) {
                final float ratio = (float) sourceWidth / sourceHeight;
                if (sourceWidth > sourceHeight) {
                    height = (int) (width / ratio);
                } else if (sourceHeight > sourceWidth) {
                    width = (int) (height * ratio);
                }
            }

            Drawable iconbackgrund = context.getResources().getDrawable(R.drawable.icon_background);
            Drawable iconMask = context.getResources().getDrawable(R.drawable.icon_mask);

            int textureWidth = iconbackgrund.getIntrinsicWidth();
            int textureHeight = iconbackgrund.getIntrinsicHeight();

            KidsZoneLog.d(KidsZoneLog.KIDS_THEME_DEBUG, "createIcon==>" + textureWidth + "==h==>" + textureHeight + "==iconsize==" + width);

            final Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight,
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = sCanvas;
            final Paint paint = new Paint();
            canvas.setBitmap(bitmap);
            iconbackgrund.setBounds(0, 0, textureWidth, textureHeight);
            iconbackgrund.draw(canvas);

            final int left = (textureWidth - width) / 2;
            final int top = (textureHeight - height) / 2;

            sOldBounds.set(icon.getBounds());
            icon.setBounds(left, top, left + width, top + height);
            icon.draw(canvas);
            icon.setBounds(sOldBounds);

            iconMask.setBounds(0, 0, textureWidth, textureHeight);
            iconMask.draw(canvas);

            Bitmap maskBitmap = Bitmap.createBitmap(textureWidth, textureHeight,
                    Bitmap.Config.ARGB_8888);
            final Canvas maskCanvas = new Canvas(maskBitmap);
            maskCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            iconbackgrund.setBounds(0, 0, textureWidth, textureHeight);
            iconbackgrund.draw(maskCanvas);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            Rect rect = new Rect(0, 0, textureWidth, textureHeight);
            maskCanvas.drawBitmap(bitmap, rect, rect, paint);

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            paint.setXfermode(null);
            maskCanvas.setBitmap(null);
            canvas.setBitmap(null);

            return maskBitmap;
        }
    }

    public static FastBitmapDrawable createIconDrawable(Bitmap icon, int size) {
        FastBitmapDrawable d = new FastBitmapDrawable(icon);
        d.setFilterBitmap(true);
        resizeIconDrawable(d, 0, 0, size, size);
        return d;
    }

    public static FastBitmapDrawable createIconDrawable(Bitmap icon, int top, int left, int bottom, int right) {
        FastBitmapDrawable d = new FastBitmapDrawable(icon);
        d.setFilterBitmap(true);
        resizeIconDrawable(d, top, left, bottom, right);
        return d;
    }

    static void resizeIconDrawable(Drawable icon, int top, int left, int bottom, int right) {
        icon.setBounds(top, left, bottom, right);
    }

    public static void clearHeadImg() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + HEAD_IMG_PATH + HEAD_IMG_NAME;
        if (path == null) {
            return;
        }
        File file;
        if (!(file = new File(path)).exists()) return;
        file.delete();
    }

    /**
     * get string id by name
     *
     * @param context
     * @param name
     * @return
     */
    public static int getStringId(Context context, String name) {
        return getIdentifier(context, name, ID_STRING_TYPE);
    }

    /**
     * get Drawable id by name
     *
     * @param context
     * @param name
     * @return
     */
    public static int getDrawableId(Context context, String name) {
        return getIdentifier(context, name, ID_DRAWABLE_TYPE);
    }

    /**
     * Get resource ID
     *
     * @param context
     * @param name
     * @param defType ps:string.array  See more of R.java
     * @return
     */
    public static int getIdentifier(Context context, String name, String defType) {
        int id = 0;

        try {
            if (context == null || name == null) {
                KidsZoneLog.e(KidsZoneLog.KIDS_SYSTEMUI_DEBUG, "name is null");
                return 0;
            }
            id = context.getResources().getIdentifier(name, defType, context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * get strings content
     *
     * @param context
     * @param name
     * @return
     */
    public static String getString(Context context, String name) {
        int id = getStringId(context, name);
        if (id == 0) {
            KidsZoneLog.e(KidsZoneLog.KIDS_SYSTEMUI_DEBUG, "ID does not exist");
            return "";
        }
        return context.getResources().getString(id);
    }

    /**
     * get Drawable content
     *
     * @param context
     * @param name
     * @return
     */
    public static Drawable getDrawable(Context context, String name) {
        int id = getDrawableId(context, name);
        if (id == 0) {
            KidsZoneLog.e(KidsZoneLog.KIDS_SYSTEMUI_DEBUG, "ID does not exist");
            return null;
        }
        return context.getResources().getDrawable(id);
    }

    public static int getDrawableResource(Context context, String name) {
        int id = getDrawableId(context, name);
        if (id == 0) {
            KidsZoneLog.e(KidsZoneLog.KIDS_SYSTEMUI_DEBUG, "ID does not exist");
            return -1;
        }
        return id;
    }

    public static String getApp(String packageName, String className) {
        return new StringBuffer().append(packageName).append(APP_FILTER_FOR_NAME).append(className).toString();
    }

    public static String[] spiltApp(String app) {
        return app.split(APP_FILTER_FOR_NAME);
    }


    public static boolean isNumeric(CharSequence cs) {
        if (cs == null && cs.length() == 0) {
            return false;
        }
        for (int i = 0; i < cs.length(); i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static String getWallpaperName(Context context, int position) {
        return getString(context, WALLPAPER_NAME + position);
    }

    public static void setBrightness(Context context, int progress) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
    }

    public static boolean isAotuBrightnessState(Context context) {
        boolean isAuto = false;
        try {
            isAuto = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            KidsZoneLog.e(KIDS_UTILS_DEBUG, "getBrightnessState:" + Log.getStackTraceString(e));
        } catch (Exception e) {
            KidsZoneLog.e(KIDS_UTILS_DEBUG, "getBrightnessState:" + Log.getStackTraceString(e));
        }
        return isAuto;
    }

    public static void setBrightnessManual(Context context) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void setBrightnessAutomatic(Context context) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    public static int getSystemBrightness(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
    }

    public static int getSystemBrightnessMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
    }

    public static void setAudio(Context context, int progress) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
    }

    public static int getAudio(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getCurrentIsEyeMode(Context context) {
        try {
            return getInt(context, KidsZoneUtil.EYE_PROTECTION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setEyeMode(Context context, int mode) {
        Settings.System.putInt(context.getContentResolver(), KidsZoneUtil.EYE_PROTECTION_MODE, mode);
    }

    public static List<String> getRecentlyWeek() {
        List<String> data = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        data.add(KidsZoneUtil.Date2String(instance.getTime()));
        for (int i = 1; i < ONE_WEEK; i++) {
            instance.add(Calendar.DAY_OF_MONTH, -1);
            data.add(KidsZoneUtil.Date2String(instance.getTime()));
        }
        return data;
    }

    public static void initMap(HashMap<String, Integer> newMap, HashMap<String, Integer> map) {
        List<String> recentlyWeek = getRecentlyWeek();
        for (String s : recentlyWeek) {
            KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "initMap: s == " + s);
            if (map == null) {
                newMap.put(s, 0);
            } else {
                if (map.get(s) != null) {
                    newMap.put(s, map.get(s));
                } else {
                    newMap.put(s, 0);
                }
            }
        }
    }

    public static HashMap<String, Integer> string2Map(String data) {
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
        }
        return map;
    }

    public static void disableStatusBar(Context context, String type) {
        KidsZoneLog.d(KIDS_MAIN_DEBUG, "disableStatusBar: type == " + type);
        try {
            Object service = context.getSystemService(STATUS_BAR_SERVICE);
            Class<?> statusBarManager = Class.forName(CLASS_STATUS_BAR_MANAGER);
            Method disable = statusBarManager.getMethod(METHOD_DISABLE,
                    int.class);
            disable.setAccessible(true);
            switch (type) {
                case DISABLE_NONE:
                    disable.invoke(service, 0x00000000);
                    break;
                case DISABLE_RECNETS:
                    disable.invoke(service, 0x00010000 | 0x01000000);
                    break;
                case DISABLE_NAV:
                    disable.invoke(service, 0x00010000 | 0x01000000 | 0x00200000);
                    break;
                case DISABLE_PHONE:
                    disable.invoke(service, 0x01000000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            KidsZoneLog.d(KIDS_MAIN_DEBUG, "STATUS_BAR_SERVICE==>" + Log.getStackTraceString(e));
        }
    }

    public static void enterKidsZoneMode(Context context, boolean enable) {
        putInt(context, "Is_In_KidsZone", enable ? 1 : 0);
    }

    public static void hideNavInKidsZone(Context context, boolean enable) {
        putInt(context, "Is_KidsZone_hide_nav", enable ? 1 : 0);
    }

    public static void sortAppForAlpha(ArrayList<AppInfo> appInfos) {
        Collections.sort(appInfos, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo a1, AppInfo a2) {
                return a1.getLabel().toString().compareTo(a2.getLabel().toString());
            }
        });
    }

    public static void sortAppForAlphaChina(ArrayList<AppInfo> appInfos) {
        Collections.sort(appInfos, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                String py1 = o1.getPinyin();
                String py2 = o2.getPinyin();
                if (isEmpty(py1) && isEmpty(py2))
                    return 0;
                if (isEmpty(py1))
                    return -1;
                if (isEmpty(py2))
                    return 1;
                String str1 = "";
                String str2 = "";
                try {
                    str1 = ((o1.getPinyin()).toUpperCase()).substring(0, 1);
                    str2 = ((o2.getPinyin()).toUpperCase()).substring(0, 1);
                } catch (Exception e) {
                }
                return str1.compareTo(str2);
            }
        });
    }

    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new
                HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();
        String output = "";

        try {
            for (int i = 0; i < input.length; i++) {
                if (java.lang.Character.toString(input[i]).
                        matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.
                            toHanyuPinyinStringArray(input[i],
                                    format);
                    output += temp[0];
                } else
                    output += java.lang.Character.toString(
                            input[i]);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }

    static boolean isEmpty(String str) {
        return "".equals(str.trim());
    }

    public static void removeSystemLockScreen(Context context, boolean isdisable) {
        try {
            Class<?> LockPatternUtils = Class.forName("com.android.internal.widget.LockPatternUtils");
            Constructor<?>[] constructors = LockPatternUtils.getConstructors();
            Object o = constructors[0].newInstance(context);
            Method setLockScreenDisabled = null;
            //modify by wanghong for bug 51908 begin
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setLockScreenDisabled = LockPatternUtils.getDeclaredMethod("setLockScreenDisabled", boolean.class, int.class);
                setLockScreenDisabled.setAccessible(true);
//                android.os.UserHandle.java

                setLockScreenDisabled.invoke(o, isdisable, Process.myUid() / PER_USER_RANGE);
                KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "removeSystemLockScreen:  === 000 ===  isdisable - > " + isdisable);
            } else {
                setLockScreenDisabled = LockPatternUtils.getDeclaredMethod("setLockScreenDisabled", boolean.class);
                setLockScreenDisabled.setAccessible(true);
                setLockScreenDisabled.invoke(o, isdisable);
            }
            //modify by wanghong for bug 51908 end
        } catch (Exception e) {
            KidsZoneLog.e(KidsZoneLog.KIDS_LOCK_DEBUG, "removeSystemLockScreen: --> " + Log.getStackTraceString(e));
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    public static void removeRecentTask(Context paramContext) {
        ActivityManager localActivityManager = (ActivityManager) paramContext.getSystemService("activity");
        Iterator localIterator = localActivityManager.getRecentTasks(50, 2).iterator();//modify by wanghong for bug 52487
        while (localIterator.hasNext()) {
            ActivityManager.RecentTaskInfo localRecentTaskInfo = (ActivityManager.RecentTaskInfo) localIterator.next();
            String str = localRecentTaskInfo.baseIntent.getComponent().getPackageName();
            if ((!str.equals("com.dollyphin.kidszone"))
                    && !str.equals("com.android.launcher3")) {
                removeTask(localActivityManager, localRecentTaskInfo.persistentId);
            }
        }
    }

    public static void removeTask(ActivityManager am, int taskId) {
        try {
            Method removeTask = ActivityManager.class.getMethod("removeTask", int.class);
            removeTask.invoke(am, taskId);
        } catch (Exception e) {
            KidsZoneLog.d(KIDS_MAIN_DEBUG, "removeTask is Exception" + e.getMessage());
        }
    }

    public static void hideNav(View v) {
        v.setSystemUiVisibility(0x01000000);
    }

    public static boolean isSupportPermission(Context context) {
        return context.getResources().getBoolean(R.bool.supports_premission);
    }

    public static int getCurrent(Context context) {
        int page = SharePrefereUtils.getCurrentPage(context);
        if (page > getPageNum(context) - 1) {
            return getPageNum(context) - 1;
        }
        return page;
    }

    private static int getPageNum(Context context) {
        final int appSize = SharePrefereUtils.getDislayAppNum(context);

        int length = appSize % KidsZoneUtil.getNumOfPage(context);
        int size = appSize / KidsZoneUtil.getNumOfPage(context);

        return length == 0 ? appSize == 0 ? 1 : size : size + 1;
    }

    public static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; //no sim
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        return result;
    }

    public static boolean isAirplaneMode(Context context) {
        return getInt(context, Settings.Global.AIRPLANE_MODE_ON, 0) == 0;
    }

    public static void setMobileDataStatus(Context context, boolean enabled) {
        try {
            KidsZoneLog.d(true, "setMobileDataStatus: enabled == " + enabled);
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> TelephonyClass = Class.forName(telephonyManager.getClass().getName());
            Method setDataEnabled = TelephonyClass.getDeclaredMethod("setDataEnabled", boolean.class);
            setDataEnabled.setAccessible(true);
            setDataEnabled.invoke(telephonyManager, enabled);
        } catch (Exception e) {
            KidsZoneLog.e(true, "setMobileDataStatus:" + Log.getStackTraceString(e));
        }
    }

    public static void setMobileDataStatus(Context context, int subId, boolean enabled) {
        try {
            KidsZoneLog.d(true, "setMobileDataStatus: enabled == " + enabled);
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> TelephonyClass = Class.forName(telephonyManager.getClass().getName());
            Method setDataEnabled = TelephonyClass.getDeclaredMethod("setDataEnabled", int.class, boolean.class);
            setDataEnabled.setAccessible(true);
            setDataEnabled.invoke(telephonyManager, subId, enabled);
        } catch (Exception e) {
            KidsZoneLog.e(true, "setMobileDataStatus:" + Log.getStackTraceString(e));
        }
    }

    public static boolean isMobileDataEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo.isConnected();
    }

    public static SubscriptionManager getSubscriptionManager(Context context) {
        return (SubscriptionManager) context.getSystemService(
                Context.TELEPHONY_SUBSCRIPTION_SERVICE);
    }

    public static String getThemeName(String name) {
        return name.replace(".", "_")
                .replace("$", "_")
                .toLowerCase();
    }

    public static float textToTextWidth(String appName) {
        float textWidth = 0;
        if (TextUtils.isEmpty(appName)) {
            KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "textToTextWidth: appName == " + appName);
            return textWidth;
        }
        TextPaint textPaint = new TextPaint();
        textWidth = textPaint.measureText(appName);
        return textWidth;
    }

    public static String catAppName(String label, final int MAX_TXT_LENGTH) {
        StringBuffer buffer = new StringBuffer();
        float textPaintWidth = textToTextWidth(label);

        if (textPaintWidth >= MAX_TXT_LENGTH) {
            char[] chars = null;
            if (!TextUtils.isEmpty(label)) {
                chars = label.toCharArray();
            }
            int length = label.getBytes().length;
            for (int i = 0; i <= length; i++) {
                buffer.append(chars[i]);
                KidsZoneLog.d(KidsZoneLog.KIDS_COLUMN_DEBUG, "catAppName: i == " + i + "      length == " + textToTextWidth(buffer.toString()));
                if (textToTextWidth(buffer.toString()) > MAX_TXT_LENGTH) {
                    break;
                }
            }
        } else {
            return label;
        }
        String toString = buffer.toString();
        return toString.substring(0, toString.length() - 2) + "...";
    }

    public static boolean isGoogleApp(String name) {
        if (name.startsWith("com.google") || name.startsWith("com.whatsapp") || name.startsWith("com.android.vending")
                || name.startsWith("com.android.music.activitymanagement.TopLevelActivity") || name.startsWith("com.instagram")) {
            return true;
        }
        return false;
    }

    public static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void setKidsZoneLockedHide(Context context, boolean hide) {
        putInt(context, "KidsZone_locked_hide", hide ? 1 : 0);
    }

    public static boolean getKidsZoneLockedHide(Context context) {
        return getInt(context, "KidsZone_locked_hide", 0) == 1;
    }

    private static boolean putInt(Context context, String key, int value) {
        if (context.getResources().getString(R.string.global_system).equals(mGlobal)) {
            return Settings.Global.putInt(context.getContentResolver(), key, value);
        } else {
            return Settings.System.putInt(context.getContentResolver(), key, value);
        }
    }

    private static int getInt(Context context, String key, int value) {
        if (context.getResources().getString(R.string.global_system).equals(mGlobal)) {
            return Settings.Global.getInt(context.getContentResolver(), key, value);
        } else {
            return Settings.System.getInt(context.getContentResolver(), key, value);
        }
    }

    private static int getInt(Context context, String key) throws Settings.SettingNotFoundException {
        if (context.getResources().getString(R.string.global_system).equals(mGlobal)){
            return Settings.Global.getInt(context.getContentResolver(), key);
        } else {
            return Settings.System.getInt(context.getContentResolver(), key);
        }
    }

    public static void inKidsZoneReceiver(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.dollyphin.kidszone.action.IN_KIDSZONE");
        context.sendBroadcast(intent);
    }

    public static void outKidsZoneReceiver(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.dollyphin.kidszone.action.OUT_KIDSZONE");
        context.sendBroadcast(intent);
    }

    public static Typeface getArialRoundedMtboldTypeface(Context context) {
        try {
            return Typeface.createFromAsset(context.getAssets(), "fonts/ARLRDBD.TTF");
        } catch (Exception e) {
            KidsZoneLog.d(KIDS_UTILS_DEBUG, "getArialRoundedMtboldTypeface: e --> " + Log.getStackTraceString(e));
        }
        return null;
    }

    public static void setTypeface(Typeface typeface, TextView... views) {
        for (TextView view : views) {
            if (view != null) {
                view.setTypeface(typeface);
            } else {
                KidsZoneLog.d(KIDS_UTILS_DEBUG, "setTypeface: views == " + views);
            }
        }

    }

    /**
     * whether the video show is in memory(packageName:com.rlk.videoshow)
     */
    public static boolean isVideoShow(Context context) {
        String packageName = "com.rlk.videoshow";
        ActivityManager manager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (Build.VERSION.SDK_INT >= 21) {
            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            for (int i = 0; i < pis.size(); i++) {
                ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(i);
                if (topAppProcess != null) {
                    if (topAppProcess.processName == null ? false : topAppProcess.processName.equals(packageName)) {
                        return true;
                    }
                }
            }
        } else {
            //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
            List localList = manager.getRunningTasks(1);
            for (int i = 0; i < localList.size(); i++) {
                ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo) localList.get(i);
                if (localRunningTaskInfo.topActivity == null ? false : localRunningTaskInfo.topActivity.getPackageName().equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
