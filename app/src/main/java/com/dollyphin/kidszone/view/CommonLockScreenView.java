/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by hong.wang on 2016/12/17.
 */
public class CommonLockScreenView extends BaseLockScreenView implements UnlockBar.OnUnlockBarChangeListener {
    private final String RES_PATH = "lock_screen_";
    private ImageView mVCommonLockContent;
    private UnlockBar mViewUnlock;
    private ImageView mVCommonOrientationPortrait;

    public CommonLockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVCommonLockContent = (ImageView) findViewById(R.id.v_common_lock_content);
        mVCommonOrientationPortrait = (ImageView) findViewById(R.id.v_common_orientation_portrait);
        mViewUnlock = (UnlockBar) findViewById(R.id.v_common_unlock);
        mViewUnlock.setOnUnlockBarChangeListener(this);
    }

    @Override
    public void onStartTrackingTouch(UnlockBar unlockBar) {

    }

    @Override
    public void onStopTrackingTouch(UnlockBar unlockBar) {
        if (unlockBar.getProgress() != unlockBar.getMax()) unlockBar.toMinProgress();
        mVCommonLockContent.setImageDrawable(KidsZoneUtil.getDrawable(getContext(), RES_PATH + unlockBar.getProgress()));
    }

    @Override
    public void onProgressChanged(UnlockBar unlockBar, int progress) {
        KidsZoneLog.d(true, "onProgressChanged:  progress === " + progress + "  max == " + unlockBar.getMax());
        if (progress == unlockBar.getMax()) {
            remove();
            return;
        }
        mVCommonLockContent.setImageDrawable(KidsZoneUtil.getDrawable(getContext(), RES_PATH + progress));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if ("P904".equals(getContext().getString(R.string.project))) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                mVCommonOrientationPortrait.setVisibility(VISIBLE);
            } else {
                mVCommonOrientationPortrait.setVisibility(GONE);
            }
        }
    }
}
