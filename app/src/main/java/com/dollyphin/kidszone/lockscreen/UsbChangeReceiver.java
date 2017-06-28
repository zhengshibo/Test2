/*Top Secret*/
package com.dollyphin.kidszone.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.view.ChargeLockScreenView;

/**
 * Created by hong.wang on 2016/12/17.
 */
public class UsbChangeReceiver extends BroadcastReceiver {
    private ChargeLockScreenView mLockScreenView;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        KidsZoneLog.i(KidsZoneLog.KIDS_LOCK_DEBUG, "UsbChangeReceiver - >onReceive() ==>" + action);
        int stringExtra = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            if ("P904".equals(context.getString(R.string.project))) {
                showView(context, intent, stringExtra);
            } else {
                showView(context, stringExtra);
            }
        }
    }

    private void showView(Context context, Intent intent, int stringExtra) {
        //add by wanghong for bug 55070 begin
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onReceive:BatteryManager.EXTRA_PLUGGED  == " + plugged);
//            if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
//                return;
//            }
        //add by wanghong for bug 55070 end

        //Current remaining capacity
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        //The maximum amount of electricity
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //Percentage of electricity
        float batteryPct = level / (float) scale;
        if (BatteryManager.BATTERY_STATUS_CHARGING == stringExtra) {
            if (null == mLockScreenView)
                mLockScreenView = (ChargeLockScreenView) LockScreenManager.getInstance(context).showLockScreen(LockScreenManager.CHARGE_VIEW);
            if (mLockScreenView != null) {
                mLockScreenView.setFullcharge(false);
                mLockScreenView.setElectricity((int) (batteryPct * 100));
            }
        } else if (BatteryManager.BATTERY_STATUS_FULL == stringExtra) {
            if (null == mLockScreenView) {
                mLockScreenView = (ChargeLockScreenView) LockScreenManager.getInstance(context).showLockScreen(LockScreenManager.CHARGE_VIEW);
            }
            if (mLockScreenView != null) mLockScreenView.setFullcharge(true);
        } else {
            if (mLockScreenView != null) {
                LockScreenManager.getInstance(context).removeView(LockScreenManager.CHARGE_VIEW);
                mLockScreenView = null;
            }
        }
    }

    private void showView(final Context context, int stringExtra) {
        boolean isCharging = BatteryManager.BATTERY_STATUS_CHARGING == stringExtra || (stringExtra == BatteryManager.BATTERY_STATUS_FULL);
        if (isCharging) {
            if (null == mLockScreenView)
                mLockScreenView = (ChargeLockScreenView) LockScreenManager.getInstance(context).showLockScreen(LockScreenManager.CHARGE_VIEW);
        } else {
            if (mLockScreenView != null)
                LockScreenManager.getInstance(context).removeView(LockScreenManager.CHARGE_VIEW);

            mLockScreenView = null;
        }
    }
}
