/*Top Secret*/
package com.dollyphin.kidszone.parent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.dollyphin.kidszone.lockscreen.LockScreenManager;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

import static com.dollyphin.kidszone.util.KidsZoneUtil.DISABLE_PHONE;
import static com.dollyphin.kidszone.util.KidsZoneUtil.DISABLE_RECNETS;

/**
 * Created by hong.wang on 2016/12/29.
 */
public class TelephonyReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context.getApplicationContext();
        String action = intent.getAction();
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onReceive: action  == " + action);
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            LockScreenManager instance = LockScreenManager.getInstance(mContext);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onCallStateChanged: ==== 000 ===== IDLE");
                    instance.showView();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onCallStateChanged: ==== 111 ===== OFFHOOK");
                    //instance.hideView();  //delete by wanghong for Emergency call
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onCallStateChanged: ==== 222 ===== RINGING");
                    KidsZoneUtil.disableStatusBar(mContext,KidsZoneUtil.DISABLE_RECNETS);//add by shenfeng for bug 54282
                    instance.hideView();
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };
}
