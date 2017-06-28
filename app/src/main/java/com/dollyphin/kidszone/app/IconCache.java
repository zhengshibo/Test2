/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.dollyphin.kidszone.theme.ThemeManager;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by feng.shen on 2017/2/4.
 */

public class IconCache {
    private Context mContext;
    private PackageManager mPm;
    private int mIconDpi;
    private ThemeManager mTm;

    public IconCache(Context context) {
        mContext = context;
        mPm = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mIconDpi = activityManager.getLauncherLargeIconDensity();
        mTm = new ThemeManager(context);
        mTm.loadAssetsManager();
    }

    public void loadIcon(AppInfo info, String name) {
        KidsZoneLog.d(KidsZoneLog.KIDS_THEME_DEBUG, "theme_name==>" + KidsZoneUtil.getThemeName(name));
        Drawable dr = mTm.loadIconForTheme(KidsZoneUtil.getThemeName(name));

        if (dr == null) {
            dr = info.getIcon(KidsZoneUtil.isGoogleApp(name) ? 320 : mIconDpi);
        }

        info.mIconBitmap = KidsZoneUtil.createIcon(mContext, dr);
        info.mIcon = dr;
    }
}
