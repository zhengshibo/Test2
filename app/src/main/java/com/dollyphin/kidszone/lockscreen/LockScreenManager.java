/*Top Secret*/
package com.dollyphin.kidszone.lockscreen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.application.BaseActivity;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.view.BaseLockScreenView;
import com.dollyphin.kidszone.view.ScreenView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feng.shen on 2016/8/27.
 */

public class LockScreenManager implements ScreenView.onLockScreenListener {

    public static final int COMMON_VIEW = 0;
    public static final int TIME_OUT_VIEW = 1;
    public static final int CHARGE_VIEW = 2;
    public static final int OTHER_VIEW = 3;
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    public final static String LOCK_SCREEN_START = "lock_screen_start";
    public final static String LOCK_SCREEN_STOP = "lock_screen_stop";

    private ScreenView mScreenView;
    public static int UNLOCK = 0;
    public static int HIDE = 1;
    public static int LOCK = 2;
    public static int lockState = UNLOCK;
    private BaseActivity mLockScreenAty;
    private List<BaseLockScreenView> mCacheLockView = new ArrayList<>();

    private LockScreenManager(Context context) {
        if (context == null) {
            KidsZoneLog.e(KidsZoneLog.KIDS_LOCK_DEBUG, "LockScreenManager: context is null");
            return;
        }
        mContext = context.getApplicationContext();
        mScreenView = new ScreenView(mContext);
        mScreenView.setonLockScreenListener(this);
        KidsZoneUtil.hideNav(mScreenView);
        initScreenView();
    }

    private static LockScreenManager mInstance;

    private Context mContext;

    public static LockScreenManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LockScreenManager(context);
        }
        return mInstance;
    }

    private BaseLockScreenView loadDisplayView(int type) {
        if (mScreenView.onInstanceof(type)) return mScreenView.getChild(type);

        if (KidsZoneUtil.getKidsZoneLockedHide(mContext)) {
            return null;
        }

        BaseLockScreenView showView = getShowView(type);
        showView.setFlag(type);
        mScreenView.addLockScreenView(showView);
        addLockView();
        mContext.sendBroadcast(new Intent().setAction(LOCK_SCREEN_START));
        return showView;
    }

    public void showView() {
        if (lockState == HIDE && (!mCacheLockView.isEmpty())) {
            KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "showView =========  ");
            addScreenViewAllChild();
            mScreenView.refreshView();
            addLockView();
        }
    }

    private void cacheLockView() {
        mCacheLockView.clear();
        int count = mScreenView.getChildCount();
        for (int i = 0; i < count; i++) {
            mCacheLockView.add((BaseLockScreenView) mScreenView.getChildAt(i));
        }
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "cacheLockView: size == " + mCacheLockView.size());
    }

    private void addScreenViewAllChild() {
        mScreenView.removeAllViews();
        int size = mCacheLockView.size();
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "addScreenViewAllChild: size == " + size);
        for (int i = 0; i < size; i++) {
            mScreenView.addView(mCacheLockView.get(i));
        }
        mCacheLockView.clear();
    }

    public void initLockAty(BaseActivity aty) {
        mLockScreenAty = aty;
    }

    private void addLockView() {
        if (lockState == LOCK) return;
        lockState = LOCK;
        try {
            //add by wanghong for bug 52323 begin
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.dollyphin.kidszone", "com.dollyphin.kidszone.lockscreen.LockScreenActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            //add by wanghong for bug 52323 end
            wm.addView(mScreenView, wmParams);
        } catch (Exception e) {
            KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "e == " + Log.getStackTraceString(e));
        }
    }

    public void hideView() {
        if (mScreenView.getChildCount() > 0) {//add by wanghong for bug 52563
            cacheLockView();
            mScreenView.hideAllView();
        }
    }

    public void rotate() {
        if (mLockScreenAty != null) {
            mLockScreenAty.finish();
            try {
                wm.removeView(mScreenView);
            } catch (Exception e) {
                KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, Log.getStackTraceString(e));
            }

        }
    }

    private void initScreenView() {
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (wmParams == null) {
            wmParams = new WindowManager.LayoutParams();
        }
//        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        wmParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    }

    public BaseLockScreenView showLockScreen(int type) {
        //add by wanghong for If the topActivity is Video Show, the lock screen is forbidden begin
        if (KidsZoneUtil.isVideoShow(mContext)) {
            return null;
        }
        //add by wanghong for If the topActivity is Video Show, the lock screen is forbidden end
        return loadDisplayView(type);
    }

    private BaseLockScreenView getShowView(int type) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        BaseLockScreenView inflate = null;
        switch (type) {
            case COMMON_VIEW:
                inflate = (BaseLockScreenView) inflater.inflate(R.layout.view_common_lock_screen, null);
                break;
            case TIME_OUT_VIEW:
                inflate = (BaseLockScreenView) inflater.inflate(R.layout.view_time_out_lock_screen, null);
                break;
            case CHARGE_VIEW:
                inflate = (BaseLockScreenView) inflater.inflate(R.layout.view_charge_lock_screen, null);
                break;
            case OTHER_VIEW:
                inflate = (BaseLockScreenView) inflater.inflate(R.layout.view_other_lock_screen, null);
                break;
        }
        return inflate;
    }


    public void removeView(int flag) {
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "removeView   flag --> " + flag);
        removeCacheLockView(flag);
        if (mScreenView.onInstanceof(flag)) {
            mScreenView.setHideView(false);
            mScreenView.removeLockScreenView(flag);
        } else if (mScreenView.getChildCount() == 0 && mCacheLockView.size() == 0) {
            removeLockView();
        }
    }

    private void removeCacheLockView(int flag) {
        int count = mCacheLockView.size();
        for (int i = count - 1; i >= 0; i--) {
            KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "removeCacheLockView: i --> " + i + "   flag  --> " + mCacheLockView.get(i).equalsFlag(flag));
            if (mCacheLockView.get(i).equalsFlag(flag)) {
                mCacheLockView.remove(i);
            }
        }
    }

    public void removeAllView() {
        lockState = UNLOCK;
        mCacheLockView.clear();
        mScreenView.removeAllViews();
        mContext.sendBroadcast(new Intent(LOCK_SCREEN_STOP));
    }

    private void removeLockView() {
        if (lockState == UNLOCK) return;
        if (mCacheLockView.size() != 0) {
            lockState = HIDE;
            mScreenView.setHideView(true);
        }
        if (mScreenView.getChildCount() == 0) {
            mCacheLockView.clear();
            mScreenView.setHideView(false);
            lockState = UNLOCK;
            mContext.sendBroadcast(new Intent(LOCK_SCREEN_STOP));
            //modify by wanghong for 56708 begin
            if (mLockScreenAty != null) mLockScreenAty.finish();
            KidsZoneUtil.hideNavInKidsZone(mContext, false);
            //modify by wanghong for 56708 end
            try {
                wm.removeView(mScreenView);
            } catch (Exception e) {
                KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, Log.getStackTraceString(e));
            }
        } else {
            lockState = LOCK;
        }
    }

    public void removeCacheView(BaseLockScreenView view) {
        if (mCacheLockView.contains(view)) {
            mCacheLockView.remove(view);
        }
    }

    @Override
    public void onHideView(BaseLockScreenView view) {
        mCacheLockView.add(view);
        lockState = HIDE;
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onHideView: ==== 000 ===");
        try {
            if (mLockScreenAty != null) mLockScreenAty.finish();
            wm.removeView(mScreenView);
        } catch (Exception e) {
            KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onRemoveView() {
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onRemoveView: ==== 111 ===");
        removeLockView();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        int count = mScreenView.getChildCount();
        for (int i = 0; i < count; i++) {
            BaseLockScreenView child = (BaseLockScreenView) mScreenView.getChild(i);
            if (child != null) {
                child.onConfigurationChanged(newConfig);
            }
        }
    }
}
