/*Top Secret*/
package com.dollyphin.kidszone.theme;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.dollyphin.kidszone.util.KidsZoneLog;

import java.lang.reflect.Method;

public class SkinPackageManager {
    private static SkinPackageManager mInstance;
    private Resources mResources;
    private Resources mResult;
    private PackageManager mPm;

    private SkinPackageManager(Context context) {
        mResources = context.getResources();
        mPm = context.getPackageManager();
    }

    public static SkinPackageManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SkinPackageManager(context);
        }

        return mInstance;
    }

    public void loadSkinAsync(String dexPath, final LoadSkinCallBack callback) {
        new AsyncTask<String, Void, Resources>() {

            protected void onPreExecute() {
                if (callback != null) {
                    callback.startloadSkin();
                }
            }

            @Override
            protected Resources doInBackground(String... params) {
                try {
                    if (params.length == 1) {
                        String dexPath_tmp = params[0];
                        KidsZoneLog.d(KidsZoneLog.KIDS_THEME_DEBUG, "==dexPath_tmp==>" + dexPath_tmp);
                        AssetManager assetManager = AssetManager.class.newInstance();
                        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                        addAssetPath.invoke(assetManager, dexPath_tmp);

                        return new Resources(assetManager, mResources.getDisplayMetrics(), mResources.getConfiguration());
                    }
                    return null;
                } catch (Exception e) {
                    return null;
                }

            }

            protected void onPostExecute(Resources result) {
                mResult = result;
                KidsZoneLog.d(KidsZoneLog.KIDS_THEME_DEBUG, "==onPostExecute==>" + result);
                if (callback != null) {
                    if (mResult != null) {
                        callback.loadSkinSuccess(mResult);
                    } else {
                        callback.loadSkinFail();
                    }
                }
            }

        }.execute(dexPath);
    }

    public Resources getResult() {
        return mResult;
    }

    public interface LoadSkinCallBack {
        void startloadSkin();

        void loadSkinSuccess(Resources resources);

        void loadSkinFail();
    }

}