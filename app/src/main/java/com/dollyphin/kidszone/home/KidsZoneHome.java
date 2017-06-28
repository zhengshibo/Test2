/*Top Secret*/
package com.dollyphin.kidszone.home;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.AppInfo;
import com.dollyphin.kidszone.app.AppLoader;
import com.dollyphin.kidszone.app.Callbacks;
import com.dollyphin.kidszone.application.BaseActivity;
import com.dollyphin.kidszone.application.KidsZoneApplication;
import com.dollyphin.kidszone.guide.Guide;
import com.dollyphin.kidszone.indicator.PageIndicator;
import com.dollyphin.kidszone.parent.TelephonyReceiver;
import com.dollyphin.kidszone.parent.VolumeReceiver;
import com.dollyphin.kidszone.systemui.StatusBar;
import com.dollyphin.kidszone.timemanager.TimeManager;
import com.dollyphin.kidszone.util.BlurBitmapUtil;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;
import com.dollyphin.kidszone.view.ExitDialog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

public class KidsZoneHome extends BaseActivity implements View.OnClickListener, Callbacks {
    private StatusBar mStatusBar;
    private ViewStub mStatusBarViewStub;
    private AppLoader mLoader;
    private Workspace mWorkspace;
    private PageIndicator mPageIndicator;
    private Guide mGuideView;
    private int year, month, day;
    private FrameLayout mDraglayer;
    private ViewStub mWallpaperViewStub;
    private WallpaperChooser mWallpaper;
    private DatePickerDialog mDateDialog;
    private ExitDialog mExitDialog;
    private Bitmap mBgDrawable;
    private AsyncTask<String, Integer, String> mHomeTask;
    private Context mContext;

    enum HomeState {NORMAL, GUIDE, WALLPAPER}

    private HomeState mState = HomeState.NORMAL;


    private int[] wallpaper = {R.drawable.setting_wallpaper_0, R.drawable.setting_wallpaper_1,
            R.drawable.setting_wallpaper_2, R.drawable.setting_wallpaper_3,
            R.drawable.setting_wallpaper_4, R.drawable.setting_wallpaper_5,
            R.drawable.setting_wallpaper_6, R.drawable.setting_wallpaper_7,
            R.drawable.setting_wallpaper_8, R.drawable.setting_wallpaper_9,
            R.drawable.setting_wallpaper_10, R.drawable.setting_wallpaper_11};
    private TelephonyReceiver mTelephoneReceiver;
    private VolumeReceiver mVolumeReceiver;

    public interface BlurBackgroundListener {
        void init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        setContentView(R.layout.kids_zone_home);
        boolean firstStart = SharePrefereUtils.isFirstStart(mContext);
        KidsZoneUtil.disableStatusBar(getApplicationContext(), firstStart ? KidsZoneUtil.DISABLE_NAV : KidsZoneUtil.DISABLE_RECNETS);
        KidsZoneUtil.enterKidsZoneMode(getApplicationContext(), true);
        KidsZoneUtil.setKidsZoneLockedHide(mContext, false);
        initView();

        if (firstStart) {
            showGuide();
            clearImg();
        } else {
            viewStubStatusBar();
            TimeManager.getInstance(getApplicationContext()).startWork();
            if ("P904".equals(getString(R.string.project)))
                TimeManager.getInstance(mContext).registerUsbReceiver(mContext);
        }
        mLoader = AppLoader.getInstance(mContext);
        mLoader.registerReceiver();
        mLoader.addCallback(this);
        mLoader.startLoader();

        mHomeTask = new HomeTask();
        mHomeTask.execute("start");

        mPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        onlayout();
        registerTelephoneReceiver();

    }

    private void initKidsData() {
        //save system Brightness Mode
        SharePrefereUtils.setSystemBrightnessMode(mContext, KidsZoneUtil.getSystemBrightnessMode(mContext));
        //save system Brightness
        SharePrefereUtils.setSystemBrightness(mContext, KidsZoneUtil.getSystemBrightness(mContext));
        //set kids zone Brightness
        KidsZoneUtil.setBrightness(mContext, SharePrefereUtils.getKidsZoneBrightness(mContext));
        //save system Audio
        SharePrefereUtils.setSystemVolume(mContext, KidsZoneUtil.getAudio(mContext));
        //set kids Zone Audio
        KidsZoneUtil.setAudio(mContext, SharePrefereUtils.getKidsZoneVolume(mContext));
        //save system eye mode
        SharePrefereUtils.setSystemEyeMode(mContext, KidsZoneUtil.getCurrentIsEyeMode(mContext));
        //set kids zone eye mode
        KidsZoneUtil.setEyeMode(mContext, (SharePrefereUtils.isKidsZoneEyeMode(mContext) ? KidsZoneUtil.EYE_MODE : 0));
        initPhoneData();
        //save system lock screen
        SharePrefereUtils.setSystemLockScreenState(this, getSystemLockScreenState());

        //remove System Lock Screen
        KidsZoneUtil.removeSystemLockScreen(this, true);
    }

    /**
     * add by wanghong for bug 52324
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void initPhoneData() {
        SubscriptionManager subscriptionManager = KidsZoneUtil.getSubscriptionManager(this);
        //save system data state
        if (!KidsZoneUtil.hasSimCard(this) || !KidsZoneUtil.isAirplaneMode(this) || !KidsZoneUtil.isMobileDataEnabled(this)) {
            SharePrefereUtils.setIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.SYSTEM_IS_OPEN_DATA, SharePrefereUtils.INVALID_INT);
        } else {
            SharePrefereUtils.setIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.SYSTEM_IS_OPEN_DATA, subscriptionManager.getDefaultSubscriptionId());
        }

        //init kids zone data state
        if (KidsZoneUtil.hasSimCard(this) && KidsZoneUtil.isAirplaneMode(this) &&
                SharePrefereUtils.getIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA) != SharePrefereUtils.INVALID_INT) {
            if (KidsZoneUtil.getSubscriptionManager(this).getActiveSubscriptionInfoList().size() == 1) {
                KidsZoneUtil.setMobileDataStatus(this, true);
            } else {
                KidsZoneUtil.setMobileDataStatus(this, SharePrefereUtils.getIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA), true);
            }
            SharePrefereUtils.setIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA, subscriptionManager.getDefaultSubscriptionId());
        } else {
            KidsZoneUtil.setMobileDataStatus(this, false);
            SharePrefereUtils.setIntParam(this, SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA, SharePrefereUtils.INVALID_INT);
        }
    }

    /**
     * add by shenfeng by bug 54282
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mState == HomeState.GUIDE) {
            KidsZoneUtil.disableStatusBar(mContext, KidsZoneUtil.DISABLE_NAV);
        }
    }

    private void registerTelephoneReceiver() {
        mTelephoneReceiver = new TelephonyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(mTelephoneReceiver, filter);
    }

    private void unRegisterTelephoneReceiver() {
        if (mTelephoneReceiver != null) {
            unregisterReceiver(mTelephoneReceiver);
        }
        mTelephoneReceiver = null;
    }

    public boolean getSystemLockScreenState() {
        try {
            Class<?> LockPatternUtils = Class.forName("com.android.internal.widget.LockPatternUtils");
            Constructor<?>[] constructors = LockPatternUtils.getConstructors();
            Object o = constructors[0].newInstance(this);
            Object invoke = null;
            //modify by wanghong for bug 51908 begin
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Method isLockScreenDisabled = LockPatternUtils.getDeclaredMethod("isLockScreenDisabled", int.class);
                isLockScreenDisabled.setAccessible(true);
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "getSystemLockScreenState: uid  = =  " + Process.myUid());
                invoke = isLockScreenDisabled.invoke(o, Process.myUid() / KidsZoneUtil.PER_USER_RANGE);
            } else {
                Method isLockScreenDisabled = LockPatternUtils.getDeclaredMethod("isLockScreenDisabled");
                isLockScreenDisabled.setAccessible(true);
                invoke = isLockScreenDisabled.invoke(o);
            }
            //modify by wanghong for bug 51908 end
            if (invoke != null) {
                if (invoke instanceof Boolean) {
                    return (Boolean) invoke;
                }
            }
        } catch (Exception e) {
            KidsZoneLog.e(KidsZoneLog.KIDS_LOCK_DEBUG, "removeSystemLockScreen: --> " + Log.getStackTraceString(e));
        }
        return false;
    }

    private void initView() {
        mDraglayer = (FrameLayout) findViewById(R.id.kids_zone_home);
        mWorkspace = (Workspace) findViewById(R.id.workspace);
        mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);

        mStatusBarViewStub = (ViewStub) findViewById(R.id.viewstub_status_bar);
        mWallpaperViewStub = (ViewStub) findViewById(R.id.viewstub_wallpaper);
        if (!"P904".equals(getString(R.string.project))) {
            TimeManager.getInstance(mContext).registerUsbReceiver(mContext);
        }
        TimeManager.getInstance(mContext).registerScreenReceiver(mContext);
        TimeManager.getInstance(mContext).registerTimeSetReceiver(mContext);//add by wanghong for bug 52723
        TimeManager.getInstance(mContext).registerSensor(mContext);
        TimeManager.getInstance(mContext).registerVolumeReceiver();//add by wanghong for bug  50995  20170109;


        //add by wanghong for  close double click power to start camera begin
        try {
            if (Settings.Secure.getInt(getContentResolver(), KidsZoneUtil.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED) != 1) {
                SharePrefereUtils.setIntParam(mContext, KidsZoneUtil.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
                        Settings.Secure.getInt(getContentResolver(), KidsZoneUtil.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED));

                Settings.Secure.putInt(getContentResolver(), KidsZoneUtil.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1/* Backwards because setting is for disabling */);
            }
        } catch (Settings.SettingNotFoundException e) {
            KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, android.util.Log.getStackTraceString(e));
        }
        //add by wanghong  for close double click power to start camera end
    }

    private void clearImg() {
        KidsZoneUtil.clearHeadImg();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBgDrawable != null && mBgDrawable.isRecycled()) {
            mBgDrawable.recycle();
            mBgDrawable = null;
        }
    }

    private void showGuide() {
        FrameLayout.LayoutParams guide = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mGuideView = (Guide) LayoutInflater.from(mContext).inflate(R.layout.guide_main, null);
        mDraglayer.addView(mGuideView, guide);
        mGuideView.addCallbacks(this);
        mGuideView.bringToFront();

        mState = HomeState.GUIDE;
    }

    public void closeGuide() {
        mLoader.updateDisplayApp();
        mGuideView.setVisibility(View.GONE);
        KidsZoneUtil.disableStatusBar(mContext, KidsZoneUtil.DISABLE_RECNETS);
        SharePrefereUtils.setNotFirstStart(getApplicationContext());
        viewStubStatusBar();
        mState = HomeState.NORMAL;
        TimeManager.getInstance(getApplicationContext()).startWork();
        if ("P904".equals(getString(R.string.project)))
            TimeManager.getInstance(mContext).registerUsbReceiver(mContext);
    }

    @Override
    public void showCalendar(String date) {
        calDate(date);
        mDateDialog = new DatePickerDialog(KidsZoneHome.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                if (mGuideView != null) {
                    mGuideView.onResult(date);
                }
            }
        }, year, month, day);
        mDateDialog.show();
        //add by wanghong for bug 54177 begin
        mDateDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(true);
        mDateDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(true);
        //modify by wanghong for bug 54177 end
    }

    @Override
    public void clear() {
        if (mWorkspace != null) {
            mWorkspace.clear();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mStatusBar != null) {
                    mStatusBar.collapse();
                }
                exitWallpaperChooseMode(-1);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mStatusBar != null) {
            mStatusBar.collapse();
        }
        if (mState == HomeState.WALLPAPER) {
            exitWallpaperChooseMode(-1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (KidsZoneUtil.isSupportPermission(mContext)) {
            checkPermissions(mPermissions);
        }
        if (mGuideView != null && SharePrefereUtils.isFirstStart(mContext)) {
            mGuideView.viewStubGuide();
        }

        TimeManager.getInstance(mContext).endOtherAppUseTime();
        updateWallpaper(SharePrefereUtils.getWallpaper(mContext));
    }

    private BlurBackgroundListener blur = new BlurBackgroundListener() {
        @Override
        public void init() {
            mStatusBar.setBlurBackgroud(BlurBitmapUtil.getBackground(mDraglayer));
        }
    };

    @Override
    public void bindScreen(final int size) {
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "bindScreen==>" + size);
        mWorkspace.init(this, size);
        mPageIndicator.addAllMarker(size, 0);
        mWorkspace.setCurrentItem(KidsZoneUtil.getCurrent(mContext));
    }

    @Override
    public void bindItem(final ArrayList<AppInfo> infos, boolean current) {
        int cpage = KidsZoneUtil.getCurrent(mContext);
        int page = mWorkspace.size();
        int N = KidsZoneUtil.getNumOfPage(mContext);
        if (current) {
            boolean full = infos.size() != 0 && (infos.size() % N == 0);
            int size = (cpage == (page - 1) && !full) ? infos.size() % N : N;
            for (int i = cpage * N; i < cpage * N + size; i++) {
                AppInfo info = infos.get(i);
                KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "bindItem==>" + info.getLabel() + "==i==>" + i + "==cpage==>" + cpage);
                BubbleTextView app = (BubbleTextView) LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.application, null);
                app.applyFromAppInfo(info);
                app.setOnClickListener(KidsZoneHome.this);
                mWorkspace.addItemToScreen(app, cpage);
            }
        } else {
            for (int i = 0; i < page; i++) {
                if (i == cpage) {
                    continue;
                }
                for (int n = i * N; n <= (i + 1) * N - 1; n++) {
                    if (n > infos.size() - 1) {
                        return;
                    }
                    AppInfo info = infos.get(n);
                    KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "bindItem==>" + info.getLabel() + "==i==>" + i + "=n=>" + n);
                    BubbleTextView app = (BubbleTextView) LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.application, null);
                    app.applyFromAppInfo(info);
                    app.setOnClickListener(KidsZoneHome.this);
                    mWorkspace.addItemToScreen(app, i);
                }
            }
        }
        mWorkspace.sync();
    }

    @Override
    public void bindGuide() {
        if (mGuideView != null) {
            mGuideView.sync();
        }
    }

    @Override
    public void closeApplication() {
        ((KidsZoneApplication) getApplication()).closeAplication();
    }

    public void closeAllAppGuideMode() {
        SharePrefereUtils.clearDislayAppNum(getApplicationContext());
        closeApplication();
    }

    private void onlayout() {
        FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) mPageIndicator.getLayoutParams();
        flp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        flp.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.pageindicator_bottom);
        mPageIndicator.setLayoutParams(flp);

        mWorkspace.setPadding(0, 0, 0, flp.bottomMargin + flp.height);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        if (v.getTag() instanceof AppInfo) {
            AppInfo info = (AppInfo) v.getTag();
            KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "onClick==>" + info.getComponentName());
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    .setComponent(info.getComponentName())
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            TimeManager.getInstance(getApplicationContext()).startOtherAppUseTime(info);
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            KidsZoneLog.i(KidsZoneLog.KIDS_CONTROL_DEBUG, "onClick: " + android.util.Log.getStackTraceString(e));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "onLongClick==>" + v.getTag());
        if (v.getTag() == null) {
            if (mState == HomeState.NORMAL) {
                enterWallpaperChooserMode();
            }
        }
        return false;
    }

    @Override
    public void onPageSelected(int pos) {
        mPageIndicator.updateMarker(pos);
        SharePrefereUtils.saveCurrentPage(getApplicationContext(), pos);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void showExitDialog() {
        mExitDialog = new ExitDialog(this);
        mExitDialog.setOnExitOkListener(onExit);
        mExitDialog.show();
    }

    private ExitDialog.OnExitOkListener onExit = new ExitDialog.OnExitOkListener() {
        @Override
        public void onOkClick(View v) {
            closeAllAppGuideMode();
        }
    };

    private void calDate(String date) {
        if (TextUtils.isEmpty(date)) {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] datestr = date.split("-");
            year = Integer.parseInt(datestr[0]);
            month = Integer.parseInt(datestr[1]) - 1;
            day = Integer.parseInt(datestr[2]);
        }
    }

    private WallpaperChooser.WallpaperChooseListener wallpaperChooseListener = new WallpaperChooser.WallpaperChooseListener() {
        @Override
        public void end(int pos) {
            SharePrefereUtils.setWallpaper(getApplicationContext(), pos);
            if (mBgDrawable != null && mBgDrawable.isRecycled()) {
                mBgDrawable.recycle();
                mBgDrawable = null;
            }
            exitWallpaperChooseMode(pos);
        }
    };

    private void enterWallpaperChooserMode() {
        mState = HomeState.WALLPAPER;

        if (mWallpaper == null) {
            mWallpaperViewStub.inflate();
            mWallpaper = (WallpaperChooser) findViewById(R.id.viewstub_wallpaper_viewpager);
        } else {
            mWallpaperViewStub.setVisibility(View.VISIBLE);
        }

        AnimatorSet anim = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mWallpaper, "scaleX", 1f, 0.62f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mWallpaper, "scaleY", 1f, 0.62f);
        anim.setDuration(500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.play(scaleX).with(scaleY);
        anim.start();

        mWorkspace.setVisibility(View.GONE);
        mPageIndicator.setVisibility(View.GONE);
        if (mStatusBar != null) {
            mStatusBar.setVisibility(View.GONE);
        }

        findViewById(R.id.viewstub_rel).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mWallpaper.dispatchTouchEventWallpaper(event);
            }
        });
        mWallpaper.setup(wallpaperChooseListener);
    }

    private void exitWallpaperChooseMode(int pos) {
        mState = HomeState.NORMAL;
        if (mWallpaperViewStub != null) {
            mWallpaperViewStub.setVisibility(View.GONE);
        }

        mWorkspace.setScaleX(0.6f);
        mWorkspace.setScaleY(0.6f);
        AnimatorSet anim = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mWorkspace, "scaleX", 0.6f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mWorkspace, "scaleY", 0.6f, 1f);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.play(scaleX).with(scaleY);
        anim.start();

        if (pos == -1) {
            pos = SharePrefereUtils.getWallpaper(mContext);
        }
        updateWallpaper(pos);

        if (mStatusBar != null) {
            mStatusBar.setVisibility(View.VISIBLE);
        }
        mWorkspace.setVisibility(View.VISIBLE);
        mPageIndicator.setVisibility(View.VISIBLE);
    }

    private void updateWallpaper(int pos) {
        mDraglayer.setBackgroundResource(wallpaper[pos]);
    }

    public HomeState getHomeState() {
        return mState;
    }

    public void setHomeState(HomeState mState) {
        this.mState = mState;
    }

    private void viewStubStatusBar() {
        mStatusBarViewStub.inflate();
        mStatusBar = (StatusBar) findViewById(R.id.status_bar);
        mStatusBar.setBlurListener(blur);
        mStatusBar.addCallback(this);
    }

    @Override
    protected void onFailedRequestPermissions(String... permissions) {
        super.onFailedRequestPermissions(permissions);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterTelephoneReceiver();
    }

    private class HomeTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "HomeTask is doInBackground==>" + params[0]);
            try {
                KidsZoneUtil.removeRecentTask(getApplicationContext());
                initKidsData();
                return "complete";
            } catch (Exception e) {
                KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "HomeTask is Exception==>" + e.getMessage());
            }

            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "HomeTask onPostExecute result ==>" + result);
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onCancelled(String result) {
            super.onCancelled(result);
        }
    }
}
