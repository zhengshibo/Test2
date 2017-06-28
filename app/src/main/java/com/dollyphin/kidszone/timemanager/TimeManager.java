/*Top Secret*/
package com.dollyphin.kidszone.timemanager;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.dollyphin.kidszone.app.AppInfo;
import com.dollyphin.kidszone.lockscreen.LockScreenManager;
import com.dollyphin.kidszone.lockscreen.UsbChangeReceiver;
import com.dollyphin.kidszone.parent.KidsSensorManager;
import com.dollyphin.kidszone.parent.VolumeReceiver;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hong.wang on 2016/11/18.
 */
public class TimeManager {
    private static final int MSG_UPDATE_INFO = 0x110;
    private static TimeManager mTimeManager;
    private ScreenReceiver mScreenReceiver;
    private Handler mHandler = new Handler();
    private boolean isTimer = false;
    private int mSingleSpace;
    private int mSingleTimer = 0;
    private int mUserTime;
    private int mOtherAppUseTime = 0;
    private TimeProvider mProvider;
    private Context mContext;
    private boolean isScreenOn = true;

    private int TIMER_INTERVALS = 1000;
    private int TIME_SPACE = 60;

    private static final HandlerThread sWorkerThread = new HandlerThread("kids-zone-timer");

    static {
        sWorkerThread.start();
    }

    private final Handler sWorker = new Handler(sWorkerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "sWorker -> handleMessage: isTimer ==> " + isTimer);
            UpDateTime();
            sWorker.sendEmptyMessageDelayed(MSG_UPDATE_INFO, TIMER_INTERVALS);
        }
    };
    private UsbChangeReceiver mUsbChangeReceiver;
    private KidsSensorManager mSensorManager;
    private TimeSetReceiver mTimeSetReceiver;
    private VolumeReceiver mVolumeReceiver;

    private TimeManager(Context context) {
        mContext = context.getApplicationContext();
        mProvider = new TimeProvider(context);
        mUserTime = mProvider.getTodayUseTime() * TIME_SPACE;
    }

    public void registerSensor(Context context) {
        mSensorManager = new KidsSensorManager(context);
    }


    public static TimeManager getInstance(Context context) {
        if (mTimeManager == null)
            mTimeManager = new TimeManager(context);
        return mTimeManager;
    }

    private void UpDateTime() {
        KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "UpDateTime: Thread id ==> " + Thread.currentThread().getId());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Timer();
                KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "Timer: Thread id ==> " + Thread.currentThread().getId());
            }
        });
    }

    public void startWork(int singleTime) {
        if (isTimer) return;
        if (!isScreenOn) return;
        mSingleSpace = isValidSingleTime(singleTime) * TIME_SPACE;
        initData(mContext);
    }

    public void startWork() {
        if (isTimer) return;
        if (!isScreenOn) return;
        updateTime();
        initData(mContext);
    }

    private void initData(Context context) {
        isTimer = true;
        sWorker.sendEmptyMessage(MSG_UPDATE_INFO);
        setZeroAlarm(context);
    }

    public void stopWork() {
        isTimer = false;
        sWorker.removeMessages(MSG_UPDATE_INFO);
        mProvider.saveAppUseTime(mUserTime / TIME_SPACE);
    }


    private int isValidSingleTime(int singleTime) {
        return singleTime == 0 ? getSingleTime() : singleTime;
    }

    private int getSingleTime() {
        return SharePrefereUtils.getUseTime(mContext);
    }

    private void Timer() {
        addSingleTime();
        addUserTime();
        addOtherAppUseTime();
    }

    private void addOtherAppUseTime() {
        mOtherAppUseTime++;
        mProvider.addOtherAppUseTime(mOtherAppUseTime / TIME_SPACE);
    }

    public void startOtherAppUseTime(AppInfo info) {
        mOtherAppUseTime = mProvider.startCountOtherAppUseTime(info);
        mOtherAppUseTime = mProvider.getAppUsedTime(info) * TIME_SPACE;
    }

    public void endOtherAppUseTime() {
        mOtherAppUseTime = 0;
        mProvider.endCountOtherAppUseTime();
    }

    private void addSingleTime() {
        mSingleTimer++;
        KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "addSingleTime: mSingleSpace == " + mSingleSpace + "  mSingleTimer == " + mSingleTimer);
        if (mSingleTimer >= mSingleSpace) {
            completeSingleSpace();
            mSingleTimer = 0;
        }
    }

    public void updateTime() {
        if (mSingleTimer == 0 || mSingleTimer >= mSingleSpace) {
            mSingleSpace = getSingleTime() * TIME_SPACE;
        }
    }

    private void completeSingleSpace() {
        mSingleTimer = 0;
        //add by wanghong for If the topActivity is Video Show, the timer continues begin
        if (!KidsZoneUtil.isVideoShow(mContext)) {
            LockScreenManager.getInstance(mContext).showLockScreen(LockScreenManager.TIME_OUT_VIEW);
            stopWork();
        }
        //add by wanghong for If the topActivity is Video Show, the timer continues end
    }

    private void addUserTime() {
        mUserTime++;
        KidsZoneLog.d(KidsZoneLog.KIDS_TIME_DEBUG, "addUserTime: mUserTime == " + mUserTime);
        mProvider.updateUseTime(mUserTime / TIME_SPACE);
    }

    /**
     * Set 0 alarm clocks
     *
     * @param context
     */
    private void setZeroAlarm(Context context) {
        Intent intent = new Intent(context, DateChangeReceiver.class);
        PendingIntent mZeroSender = PendingIntent.getBroadcast(context, 0, intent, 0);
        setAlarm(mZeroSender, context, 0, 0);
    }

    private void setAlarm(PendingIntent sender, Context context, int hour, int minute) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, 24);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 1);

        long timeInMillis = calendar.getTimeInMillis();
        if (System.currentTimeMillis() > timeInMillis)
            timeInMillis += AlarmManager.INTERVAL_DAY;
        manager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
        KidsZoneLog.i(KidsZoneLog.KIDS_TIME_DEBUG, "setAlarm hour ==> " + hour + "   minute  ==>  " + minute + " timeInMillis ==>   " + new Date(timeInMillis).toString());
    }

    public void registerScreenReceiver(Context context) {
        if (mScreenReceiver != null) return;
        mScreenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(LockScreenManager.LOCK_SCREEN_STOP);
        filter.addAction(LockScreenManager.LOCK_SCREEN_START);
        context.registerReceiver(mScreenReceiver, filter);
    }

    public void registerUsbReceiver(Context context) {
        if (mUsbChangeReceiver != null) return;
        mUsbChangeReceiver = new UsbChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        filter.addAction("android.hardware.usb.action.USB_STATE");
        context.registerReceiver(mUsbChangeReceiver, filter);
    }

    /**
     * add by wanghong for bug 52723
     * @param context
     */
    public void registerTimeSetReceiver(Context context) {
        if (mTimeSetReceiver != null) return;
        mTimeSetReceiver = new TimeSetReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        context.registerReceiver(mTimeSetReceiver, filter);
    }

    public void unRegisterReceiver(Context context) {
        if (mScreenReceiver != null) context.unregisterReceiver(mScreenReceiver);
        if (mUsbChangeReceiver != null) context.unregisterReceiver(mUsbChangeReceiver);
        if (mTimeSetReceiver != null) context.unregisterReceiver(mTimeSetReceiver);
        if (mVolumeReceiver != null) context.unregisterReceiver(mVolumeReceiver);//add by wanghong for bug  50995  20170109
        mVolumeReceiver = null;
        mScreenReceiver = null;
        mUsbChangeReceiver = null;
        mTimeSetReceiver = null;
        mSensorManager.unregisterSensor();
    }

    /**
     * add by wanghong for bug  50995  20170109
     */
    public void registerVolumeReceiver() {
        mVolumeReceiver = new VolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(VolumeReceiver.ACTION);
        mContext.registerReceiver(mVolumeReceiver, filter);
    }

    class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            KidsZoneLog.i(KidsZoneLog.KIDS_TIME_DEBUG, "ScreenReceiver - >onReceive() ==>" + action);
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                isScreenOn = false;
                LockScreenManager.getInstance(context).showLockScreen(LockScreenManager.COMMON_VIEW);
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                isScreenOn = true;
                LockScreenManager.getInstance(context).showLockScreen(LockScreenManager.COMMON_VIEW);
            } else if (LockScreenManager.LOCK_SCREEN_STOP.equals(action)) {
//                removeSystemLockScreen();
                startWork();
            } else if (LockScreenManager.LOCK_SCREEN_START.equals(action)) {
                stopWork();
            }
        }
    }


    public static class DateChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            KidsZoneLog.i(KidsZoneLog.KIDS_TIME_DEBUG, "DateChangeReceiver - >onReceive() ==>" + action);
            //add by wanghong for bug 52723 begin
            mTimeManager.mOtherAppUseTime = 0;
            mTimeManager.mUserTime = 0;
            //add by wanghong for bug 52723 end
            mTimeManager.setZeroAlarm(context);
            mTimeManager.mProvider.setToday(new Date());
        }
    }

    /**
     * add by wanghong for bug 52723
     */
    public static class TimeSetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            KidsZoneLog.i(KidsZoneLog.KIDS_TIME_DEBUG, "TimeSetReceiver - >onReceive() ==>" + action);
            mTimeManager.mOtherAppUseTime = 0;
            mTimeManager.mUserTime = 0;
            mTimeManager.mProvider.setToday(new Date());
            mTimeManager.setZeroAlarm(context);
        }
    }
}
