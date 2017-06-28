/*Top Secret*/
package com.dollyphin.kidszone.parent;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.dollyphin.kidszone.lockscreen.LockScreenManager;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.view.BaseLockScreenView;

/**
 * Created by hong.wang on 2016/12/16.
 */
public class KidsSensorManager {

    private BaseLockScreenView screenView;
    private long time;
    private Context mContent;
    private final SensorManager mSensorManager;

    public KidsSensorManager(final Context context) {
        mContent = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor acceleromererSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(sensorEventListener, acceleromererSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float z = event.values[SensorManager.DATA_Z];

            KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "KidsSensorManager: z == " + z);

            if (z < -6) {
                screenView = LockScreenManager.getInstance(mContent).showLockScreen(LockScreenManager.OTHER_VIEW);
            } else if (z > 0) {
                if (screenView != null && System.currentTimeMillis() - time > 600) {
                    LockScreenManager.getInstance(mContent).removeView(LockScreenManager.OTHER_VIEW);
                    time = System.currentTimeMillis();
                    screenView = null;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void unregisterSensor() {
        if (mSensorManager != null)
            this.mSensorManager.unregisterListener(this.sensorEventListener);
    }
}
