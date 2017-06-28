/*Top Secret*/
package com.dollyphin.kidszone.application;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by feng.shen on 2016/12/16.
 */

public class DeviceProfile {
    public int mIconDpi;

    public DeviceProfile(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mIconDpi = activityManager.getLauncherLargeIconDensity();
    }
}
