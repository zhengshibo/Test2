/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dollyphin.kidszone.util.KidsZoneLog;

/**
 * Created by feng.shen on 2016/12/27.
 */

public class AppInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppLoader appLoader = AppLoader.getInstance(context);
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "AppInstallReceiver==>" + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            appLoader.onPackageAdd(packageName);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            appLoader.onPackageRemove(packageName);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            appLoader.onPackageReplace(packageName);
        }
    }
}
