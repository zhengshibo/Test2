/*Top Secret*/
package com.dollyphin.kidszone.lockscreen;

import android.content.res.Configuration;
import android.os.Bundle;

import com.dollyphin.kidszone.application.BaseActivity;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * add by wanghong for bug 52323
 */
public class LockScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lock_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LockScreenManager.getInstance(this).initLockAty(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishing()) {
            if (LockScreenManager.lockState == LockScreenManager.LOCK) {
                KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), true);
            } else {
                KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), false);
            }
        }
        if (KidsZoneUtil.getKidsZoneLockedHide(getApplicationContext())) {
            LockScreenManager.getInstance(this).removeAllView();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LockScreenManager.getInstance(this).onConfigurationChanged(newConfig);
    }
}
