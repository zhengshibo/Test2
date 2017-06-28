/*Top Secret*/
package com.dollyphin.kidszone.parent;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.SharePrefereUtils;

/**
 * Created by hong.wang on 2017/1/9.
 *
 * add  for bug  50995  20170109
 */

public class VolumeReceiver extends BroadcastReceiver {
    public static final String ACTION = "android.media.VOLUME_CHANGED_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onReceive:  intent == " + intent);
        if (ACTION.equals(action)) {
            int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            SharePrefereUtils.setKidsZoneVolume(context, streamVolume);
        }
    }
}
