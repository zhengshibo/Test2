/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;

import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class AppLoader {

    private static AppLoader mAppLoader;
    //private static HandlerThread sWorkThread = new HandlerThread("app-loader");

    //static {
    //    sWorkThread.start();
    //}

    private Handler mHandler = new Handler();
    //private Handler sWork;
    private Context mContext;
    private PackageManager mPm;
    private AllAppList sAllApps;
    private BlackFilter mBlackFilter;
    private LimitFilter mLimitFilter;
    private static Callbacks mCallbacks;
    private AppInstallReceiver mInstallReceiver;
    private IconCache mIconCache;
    private AppFilterCallback mAppFilterCallback;

    private static int N = 0;

    private AppLoader(Context context) {
        mContext = context.getApplicationContext();
        mPm = mContext.getPackageManager();
        mBlackFilter = new BlackFilter(context);
        mLimitFilter = new LimitFilter(context);
        sAllApps = new AllAppList(context, mBlackFilter, mLimitFilter);
        mIconCache = new IconCache(context);
        N = KidsZoneUtil.getNumOfPage(context);
//        registerReceiver();
    }

    public static AppLoader getInstance(Context context) {
        if (mAppLoader == null) {
            mAppLoader = new AppLoader(context);
        }
        return mAppLoader;
    }

    public void addCallback(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void startLoader() {
        //sWork = new Handler(sWorkThread.getLooper());
        LoaderTask loaderTask = new LoaderTask();
        mHandler.post(loaderTask);
    }

    private class LoaderTask implements Runnable {
        public LoaderTask() {

        }

        @Override
        public void run() {
            loadAndBindWorkspace();
        }
    }

    private void loadAndBindWorkspace() {
        synchronized (this) {
            loadAllApp();
            bindScreen();
            bindGuide();
            bindWorkspace();
        }
    }

    private void loadAllApp() {
        List<ResolveInfo> infos = getAllAppList();
        sAllApps.clear();
        Collections.sort(infos, new ResolveInfo.DisplayNameComparator(mContext.getPackageManager()));
        int i = 0;
        for (ResolveInfo info : infos) {
            sAllApps.add(new AppInfo(mContext, info, i, mIconCache));
            i++;
        }
        SharePrefereUtils.setDislayAppNum(mContext, AppProvider.display.size());
    }

    private List<ResolveInfo> getAllAppList() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return mPm.queryIntentActivities(intent, 0);
    }

    private void bindWorkspace() {
        bindEarly();
        bindOther();
    }

    private void bindEarly() {
        bindItem(AppProvider.display, true);
    }

    private void bindOther() {
        bindItem(AppProvider.display, false);
    }

    private boolean isShouldEarly() {
        int displayNum = SharePrefereUtils.getDislayAppNum(mContext);
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "==isShouldEarly==>" + displayNum);
        return displayNum > N;
    }

    private int getCurrentSize(int page) {
        if (page == getPageNum() - 1) {
            return SharePrefereUtils.getDislayAppNum(mContext) % N;
        }

        return N;
    }


    public void updateDisplayApp() {
        bindScreen();
        bindWorkspace();
    }

    public void updateDisplayApp(String app, boolean display) {
        SharePrefereUtils.updateDisplayApp(mContext, app, display);
        if (display) {
            addDisplayApp(app);
        } else {
            removeDisplayApp(app);
        }
    }

    public void removeDisplayApp(String app) {
        AppInfo info = sAllApps.getApp(app);
        if (info == null) {
            return;
        }
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "removeDisplayApp==>" + AppProvider.display.contains(info) + "==app==>" + info.getLabel());
        if (AppProvider.display.contains(info)) {
            AppProvider.remove(info);
        }
        SharePrefereUtils.setDislayAppNum(mContext, AppProvider.display.size());
    }

    public void addDisplayApp(String app) {
        AppInfo info = sAllApps.getApp(app);
        if (info == null) {
            return;
        }
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "addDisplayApp==>" + AppProvider.display.contains(info) + "==app==>" + info.getLabel());
        if (!AppProvider.display.contains(info)) {
            int index = sAllApps.getIndex(info.getIndex());
            AppProvider.display.add(index, info);
        }
        SharePrefereUtils.setDislayAppNum(mContext, AppProvider.display.size());
    }

    public void sync() {
        bindScreen();
        bindWorkspace();
    }

    private void bindGuide() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallbacks.bindGuide();
            }
        });
    }

    private void bindItem(final ArrayList<AppInfo> infos, final boolean current) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallbacks.bindItem(infos, current);
            }
        });
    }

    private void bindScreen() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallbacks.bindScreen(getPageNum());
            }
        });
    }

    private int getPageNum() {
        final int appSize = SharePrefereUtils.getDislayAppNum(mContext);

        int length = appSize % N;
        int size = appSize / N;
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "getPageNum==>" + length + "==size==>" + size);
        return length == 0 ? appSize == 0 ? 1 : size : size + 1;
    }

    public void registerReceiver() {
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "registerReceiver==>");
        mInstallReceiver = new AppInstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        mContext.registerReceiver(mInstallReceiver, filter);
    }

    public void unRegisterReceiver() {
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "unRegisterReceiver==>");
        if (mInstallReceiver != null) {
            mContext.unregisterReceiver(mInstallReceiver);
        }
        mInstallReceiver = null;
    }

    public void onPackageAdd(String packageName) {
        loadAllApp();
        updateDisplayApp();
        addAppManager(packageName);
    }

    public void onPackageRemove(final String packageName) {
        for (int i = AppProvider.data.size() - 1; i >= 0; i--) {
            AppInfo info = AppProvider.data.get(i);
            if (info.getPackageName().equals(packageName)) {
                AppProvider.data.remove(info);
                removeAppManager(info);
                break;
            }
        }

        for (int i = AppProvider.display.size() - 1; i >= 0; i--) {
            AppInfo info = AppProvider.display.get(i);
            if (info.getPackageName().equals(packageName)) {
                AppProvider.display.remove(info);
                SharePrefereUtils.setDislayAppNum(mContext, AppProvider.display.size());
                SharePrefereUtils.updateDisplayApp(mContext, KidsZoneUtil.getApp(info.getPackageName(), info.getClassName()), false);
                break;
            }
        }
        updateDisplayApp();
    }

    public void onPackageReplace(String packageName) {

    }

    private void removeAppManager(final AppInfo info) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAppFilterCallback != null) mAppFilterCallback.removeApp(info);
            }
        });
    }

    private void addAppManager(final String packageName) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAppFilterCallback != null) {
                    int size = AppProvider.data.size();
                    for (int i = 0; i < size; i++) {
                        if (AppProvider.data.get(i).getPackageName().equals(packageName)) {
                            mAppFilterCallback.addApp(i, AppProvider.data.get(i));
                            SharePrefereUtils.updateDisplayApp(mContext, KidsZoneUtil.getApp(AppProvider.data.get(i).getPackageName(), AppProvider.data.get(i).getClassName()), false);
                            break;
                        }
                    }
                }
            }
        });
    }

    public void registerAppFilterCallBack(AppFilterCallback callback) {
        mAppFilterCallback = callback;
    }

    public void unregisterAppFilterCallBack() {
        mAppFilterCallback = null;
    }
}
