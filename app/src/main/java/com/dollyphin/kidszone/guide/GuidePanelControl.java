/*Top Secret*/
package com.dollyphin.kidszone.guide;

import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.view.DialogCallbacks;

/**
 * Created by feng.shen on 2016/12/12.
 */

public class GuidePanelControl {
    private DialogCallbacks mCallback;
    public static final int WELCOME_MODE = 0;
    public static final int INFORMATION_MODE = 1;
    public static final int PASSWORD_MODE = 2;
    public static final int APP_MODE = 3;
    public static final int TIMER_MODE = 4;

    private int mCurrentMode = WELCOME_MODE;

    public GuidePanelControl(DialogCallbacks callback) {
        mCallback = callback;
    }

    public void syncMode() {
        KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "sync==>" + mCurrentMode);
        switch (mCurrentMode) {
            case WELCOME_MODE:
                mCallback.setEnable(false, true);
                break;
            case INFORMATION_MODE:
                mCallback.setEnable(true, false);
                break;
            case PASSWORD_MODE:
                mCallback.setEnable(true, false);
                break;
            case APP_MODE:
                mCallback.updateNext(true);
                mCallback.setEnable(true, true);
                break;
            case TIMER_MODE:
                mCallback.updateNext(false);
                mCallback.setEnable(true, true);
                break;
            default:
                break;
        }

        if (mCallback != null) {
            mCallback.change(mCurrentMode);
        }

        if (mCallback != null && mCurrentMode == INFORMATION_MODE) {
            mCallback.check();
        }

        if (mCallback != null && mCurrentMode > TIMER_MODE) {
            mCallback.close();
        }
    }

    public void setMode(int mode) {
        mCurrentMode = mode;
        syncMode();
    }

    public void next() {
        if (mCurrentMode == TIMER_MODE) {
            mCallback.close();
            return;
        }
        mCurrentMode++;
        syncMode();
    }

    public void back() {
        if (mCurrentMode == WELCOME_MODE) {
            return;
        }
        mCurrentMode--;
        syncMode();
    }

    public void exit() {
        if (mCallback != null) {
            mCallback.closeApplication();
        }
    }
}
