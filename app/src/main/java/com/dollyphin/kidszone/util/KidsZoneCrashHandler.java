/*Top Secret*/
package com.dollyphin.kidszone.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.dollyphin.kidszone.application.KidsZoneApplication;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import static com.dollyphin.kidszone.util.KidsZoneLog.KIDS_CRASH_DEBUG;

/**
 * Created by feng.shen on 2016/11/23.
 */

public class KidsZoneCrashHandler implements Thread.UncaughtExceptionHandler {

    private static KidsZoneCrashHandler INSTANCE = new KidsZoneCrashHandler();
    private Context mContext;
    private KidsZoneApplication app;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> infos = new HashMap<String, String>();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

    private KidsZoneCrashHandler() {
    }

    public static KidsZoneCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * @throws
     * @Title: init
     */
    public void init(Context context, KidsZoneApplication app) {
        this.app = app;
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            app.closeAplication();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        collectDeviceInfo(mContext);
        saveCrashInfoFile(ex);
        return true;
    }

    public void collectDeviceInfo(Context ctx) {
        String versionName = KidsZoneUtil.getVersion(ctx);
        String versionCode = KidsZoneUtil.getVersionCode(ctx);

        infos.put("versionName", versionName);
        infos.put("versionCode", versionCode);

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                KidsZoneLog.e(KIDS_CRASH_DEBUG, "an error occured when collect crash info==>" + e);
            }
        }
    }

    private String saveCrashInfoFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = KidsZoneUtil.getVersion(app.getApplicationContext()) + "-crash-" + time + "-" + timestamp +
                    (KidsZoneLog.isLoggable(false) ? KidsZoneUtil.KIDS_CRASH_TXT_SUFFIX : KidsZoneUtil.KIDS_CRASH_SUFFIX);
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory()
                        .getAbsolutePath().toString() + KidsZoneUtil.KIDS_CRASH_FILE;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            KidsZoneLog.e(KIDS_CRASH_DEBUG, "an error occured while writing file...==>" + e);
        }

        return null;
    }

}
