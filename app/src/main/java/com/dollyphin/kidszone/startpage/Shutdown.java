/*Top Secret*/
package com.dollyphin.kidszone.startpage;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.lockscreen.LockScreenManager;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by feng.shen on 2017/2/23.
 */

public class Shutdown extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.black);
        LockScreenManager.getInstance(getApplicationContext()).rotate();
        KidsZoneLog.d(true, "Shutdown is start to KidsZone===>" + KidsZoneUtil.getKidsZoneLockedHide(getApplicationContext()));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
