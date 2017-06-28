/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dollyphin.kidszone.R;

/**
 * Created by hong.wang on 2016/12/16.
 */
public class OtherLockScreenView extends BaseLockScreenView {

    private ImageView mImageView;
    private AnimationDrawable mBearAnimation;
    private Handler mHandler = new Handler();
    private AnimationDrawable mRepeatAnimation;
    private ImageView mImageOrientationPortraitView;

    public OtherLockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if ("P904".equals(getContext().getString(R.string.project))) {
            mImageView = (ImageView) findViewById(R.id.v_other_lock_content);
            mImageOrientationPortraitView = (ImageView) findViewById(R.id.v_other_orientation_portrait);

            mBearAnimation = (AnimationDrawable) mImageView.getDrawable();
            mBearAnimation.start();

            int duration = 0;
            for (int i = 0; i < mBearAnimation.getNumberOfFrames(); i++) {
                duration += mBearAnimation.getDuration(i);
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageResource(R.drawable.anim_poor_form_repeat);
                    mRepeatAnimation = (AnimationDrawable) mImageView.getDrawable();
                    mRepeatAnimation.start();
                }
            }, duration);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBearAnimation != null) mBearAnimation.stop();
        if (mRepeatAnimation != null) mRepeatAnimation.stop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ("P904".equals(getContext().getString(R.string.project))) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                mImageOrientationPortraitView.setVisibility(VISIBLE);
            }else {
                mImageOrientationPortraitView.setVisibility(GONE);
            }
        }
    }
}
