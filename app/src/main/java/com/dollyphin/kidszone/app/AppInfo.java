/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class AppInfo {
    private ComponentName componentName;
    private ResolveInfo mResolveInfo;
    private PackageManager mPm;
    private ActivityInfo mActivityInfo;
    private String mPackageName;
    private String mClassName;
    private int useTime = 0;
    private String mLabel = "";
    private String mPinyin = "";
    private int mIndex = -1;
    private IconCache mIconCache;
    public Bitmap mIconBitmap;
    public Drawable mIcon;

    Intent intent = new Intent(Intent.ACTION_MAIN, null);

    public AppInfo(Context context, String packageName, String name) {
        mPackageName = packageName;
        mClassName = name;
        componentName = new ComponentName(packageName, name);
        mPm = context.getPackageManager();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(packageName, name);
        mResolveInfo = mPm.queryIntentActivities(intent, 0).get(0);
        mActivityInfo = mResolveInfo.activityInfo;
    }

    public AppInfo(String packageName, String name) {
        mPackageName = packageName;
        mClassName = name;
    }

    public AppInfo(Context context, ResolveInfo info, int index, IconCache iconCache) {
        mPm = context.getPackageManager();
        mResolveInfo = info;
        mActivityInfo = info.activityInfo;
        mPackageName = mActivityInfo.packageName;
        mClassName = mActivityInfo.name;
        componentName = new ComponentName(mPackageName, mClassName);
        mIndex = index;
        mIconCache = iconCache;
    }

    public AppInfo(PackageManager pm, ResolveInfo info) {
        mPm = pm;
        mResolveInfo = info;
        mPackageName = info.activityInfo.packageName;
        mClassName = info.activityInfo.name;
        mLabel = info.loadLabel(pm).toString();
        mPinyin = KidsZoneUtil.getPingYin(mLabel.toString());
    }

    public AppInfo(Context context, String packageName, String name, int time) {
        mPackageName = packageName;
        mClassName = name;
        useTime = time;
        componentName = new ComponentName(packageName, name);
        mPm = context.getPackageManager();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(packageName, name);
        mResolveInfo = mPm.queryIntentActivities(intent, 0).get(0);
        mActivityInfo = mResolveInfo.activityInfo;
    }

    public Drawable getIcon(int density) {
        int iconRes = mActivityInfo.getIconResource();
        Resources mRes;
        Drawable icon = null;
        if (density != 0 && iconRes != 0) {
            try {
                mRes = mPm.getResourcesForApplication(mActivityInfo.packageName);
                icon = mRes.getDrawableForDensity(iconRes, density, null);
            } catch (Exception e) {
                KidsZoneLog.e(KidsZoneLog.KIDS_APP_DEBUG, "==AppInfo getIcon is Exception==" + e.getMessage());
            }

            if (icon == null) {
                icon = mResolveInfo.loadIcon(mPm);
            }

            if (icon == null) {
                mRes = Resources.getSystem();
                icon = mRes.getDrawableForDensity(android.R.mipmap.sym_def_app_icon, density, null);
            }
        }

        return icon;
    }

    public Bitmap getBadgeIcon() {
        if (mIconBitmap == null && mIconCache != null) {
            mIconCache.loadIcon(this, mClassName);
        }
        return mIconBitmap;
    }

    public Drawable getIcon() {
        if (mIcon == null && mIconCache != null) {
            mIconCache.loadIcon(this, mClassName);
        }
        return mIcon;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public ActivityInfo getActivityInfo() {
        return mActivityInfo;
    }

    public String getLabel() {
        if (TextUtils.isEmpty(mLabel)) {
            mLabel = mResolveInfo.loadLabel(mPm).toString();
        }
        return mLabel;
    }

    public int getLabelId() {
        return mResolveInfo.labelRes;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    public int getUseTime() {
        return useTime;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getClassName() {
        return mClassName;
    }

    public String getAppName() {
        return KidsZoneUtil.getApp(mPackageName, mClassName);
    }

    public String getPinyin() {
        if (TextUtils.isEmpty(mPinyin)) {
            mPinyin = KidsZoneUtil.getPingYin(getLabel().toString());
        }
        return mPinyin;
    }

    public int getIndex() {
        return mIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AppInfo) {
            AppInfo info = (AppInfo) o;
            return mPackageName.equals(info.getPackageName()) && mClassName.equals(info.getClassName());
        }
        return false;
    }
}
