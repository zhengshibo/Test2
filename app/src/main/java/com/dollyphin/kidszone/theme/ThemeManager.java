/*Top Secret*/
package com.dollyphin.kidszone.theme;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.dollyphin.kidszone.util.KidsZoneLog;
import com.pad.themeparcel.ThemeParcel;

import java.io.File;
import java.io.InputStream;

/**
 * Created by feng.shen on 2016/8/23.
 */

public class ThemeManager {
    private Context mContext;
    private Resources newResources;

    public ThemeManager(Context context) {
        mContext = context;
    }

    Context slaveContext;

    public Resources loadAssetsManager() {
        File dir = new File("/system/theme/KidsZoneTheme/KidsZoneTheme.apk");
        if (dir.exists()) {
            SkinPackageManager.getInstance(mContext).loadSkinAsync(dir.getAbsolutePath(), new SkinPackageManager.LoadSkinCallBack() {

                @Override
                public void startloadSkin() {
                }

                @Override
                public void loadSkinSuccess(Resources resources) {
                    newResources = resources;
                    KidsZoneLog.d(KidsZoneLog.KIDS_THEME_DEBUG, "==loadIconForTheme==>" + newResources);
                }

                @Override
                public void loadSkinFail() {
                }
            });
        }

        return null;
    }

    public Drawable loadIconForTheme(String name) {
        KidsZoneLog.d(KidsZoneLog.KIDS_THEME_DEBUG, "==loadIconForTheme==>" + newResources + "==name==>" + name);
        ThemeParcel themeParcel = new ThemeParcel();
        Drawable icon = themeParcel.getThemeDrawableResource(name, newResources, "com.theme.kidszone");
        return icon;
    }

    public Bitmap loadAssetsForSkin(String path) {
        Resources resources = SkinPackageManager.getInstance(mContext).getResult();
        InputStream in;
        try {
            in = resources.getAssets().open(path);
            Bitmap bmp = BitmapFactory.decodeStream(in);
            return bmp;
        } catch (Exception e) {
        }
        return null;
    }

    public Bitmap loadAssetsForContext(String path) {
        InputStream in;
        try {
            try {
                slaveContext = mContext.createPackageContext(
                        "com.example.fengshen.testtheme", Context.CONTEXT_IGNORE_SECURITY);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            in = slaveContext.getResources().getAssets().open(path);
            Bitmap bmp = BitmapFactory.decodeStream(in);
            return bmp;
        } catch (Exception e) {

        }
        return null;
    }
}
