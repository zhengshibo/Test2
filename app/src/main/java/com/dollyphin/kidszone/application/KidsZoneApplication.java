/*Top Secret*/
package com.dollyphin.kidszone.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;

import com.dollyphin.kidszone.app.AppLoader;
import com.dollyphin.kidszone.timemanager.TimeManager;
import com.dollyphin.kidszone.util.KidsZoneCrashHandler;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.ArrayList;

/**
 * Created by feng.shen on 2016/11/23.
 */

public class KidsZoneApplication extends Application {
    private ArrayList<Activity> activities = new ArrayList<>();
    public static KidsZoneApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        KidsZoneUtil.inKidsZoneReceiver(this);
        KidsZoneCrashHandler crashHandler = KidsZoneCrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), this);
        if (activities.size() != 0) {
            activities.clear();
        }
        mInstance = this;
    }

    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    public void closeAplication() {
        for (Activity activity : activities) {
            if (activity != null) {
                KidsZoneLog.d(KidsZoneLog.KIDS_CRASH_DEBUG, "closeAplication");
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                activity.finish();
            }
        }
        KidsZoneUtil.outKidsZoneReceiver(mInstance);
        resetSystemData();
        Intent localIntent = new Intent("android.intent.action.MAIN",
                null);
        localIntent.addCategory("android.intent.category.HOME");
        localIntent.addCategory("android.intent.category.DEFAULT");
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(localIntent);
    }

    public void resetSystemData() {
        KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), false);
        KidsZoneUtil.enterKidsZoneMode(getApplicationContext(), false);
        KidsZoneUtil.disableStatusBar(getApplicationContext(), KidsZoneUtil.DISABLE_NONE);
        try {
            TimeManager.getInstance(this).stopWork();
            TimeManager.getInstance(this).unRegisterReceiver(this);
            AppLoader.getInstance(getApplicationContext()).unRegisterReceiver();
            if (SharePrefereUtils.getSystemBrightnessMode(this) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                KidsZoneUtil.setBrightnessAutomatic(this);
            } else {
                //set system Brightness
                KidsZoneUtil.setBrightness(getApplicationContext(), SharePrefereUtils.getSystemBrightness(getApplicationContext()));
            }
            //set system Audio
            KidsZoneUtil.setAudio(getApplicationContext(), SharePrefereUtils.getSystemVolume(getApplicationContext()));
            //set system eye mode
            if (!SharePrefereUtils.getSystemLockScreenState(this)) {
                KidsZoneUtil.removeSystemLockScreen(this, false);
            }
            KidsZoneUtil.setEyeMode(getApplicationContext(), SharePrefereUtils.isSystemEyeMode(getApplicationContext()) ? KidsZoneUtil.EYE_MODE : 0);
            KidsZoneUtil.removeRecentTask(getApplicationContext());
            // add by wanghong for bug 52324 begin
            SubscriptionManager subscriptionManager = KidsZoneUtil.getSubscriptionManager(this);
            if (KidsZoneUtil.hasSimCard(this) && KidsZoneUtil.isAirplaneMode(this) && SharePrefereUtils.getIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.SYSTEM_IS_OPEN_DATA) != SharePrefereUtils.INVALID_INT) {
                if (subscriptionManager.getActiveSubscriptionInfoList().size() == 1) {
                    KidsZoneUtil.setMobileDataStatus(this, true);
                } else {
                    KidsZoneUtil.setMobileDataStatus(this, SharePrefereUtils.getIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.SYSTEM_IS_OPEN_DATA), true);
                }
                SharePrefereUtils.setIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.SYSTEM_IS_OPEN_DATA, subscriptionManager.getDefaultSubscriptionId());
            } else {
                KidsZoneUtil.setMobileDataStatus(this, false);
                SharePrefereUtils.setIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.SYSTEM_IS_OPEN_DATA, SharePrefereUtils.INVALID_INT);
            }
            // add by wanghong for bug 52324 end
            if (SharePrefereUtils.getIntParam(this, KidsZoneUtil.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED) != SharePrefereUtils.INVALID_INT) {/*add by wanghong for recovery double click power to start camera  */
                Settings.Secure.putInt(getContentResolver(), KidsZoneUtil.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
                        SharePrefereUtils.getIntParam(this, KidsZoneUtil.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED));
            }
        } catch (Exception e) {
            KidsZoneLog.d(true, "closeAplication: e == " + Log.getStackTraceString(e));
        }
    }

    public static void closeApp() {
        mInstance.closeAplication();
    }
}
