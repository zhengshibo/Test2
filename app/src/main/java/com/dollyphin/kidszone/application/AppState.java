/*Top Secret*/
package com.dollyphin.kidszone.application;

import android.content.Context;

/**
 * Created by feng.shen on 2016/12/16.
 */

public class AppState {
    private static AppState INSTANCE;
    private DeviceProfile mDeviceProfile;

    private AppState(Context context) {
        mDeviceProfile = new DeviceProfile(context);
    }

    public static AppState getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppState(context);
        }
        return INSTANCE;
    }

    public DeviceProfile getDeviceProfile() {
        return mDeviceProfile;
    }
}
