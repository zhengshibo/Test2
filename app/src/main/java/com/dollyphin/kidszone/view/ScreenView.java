/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.app.Service;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.application.KidsZoneApplication;
import com.dollyphin.kidszone.lockscreen.LockScreenManager;
import com.dollyphin.kidszone.util.KidsZoneLog;

/**
 * Created by hong.wang on 2017/1/17.
 */

public class ScreenView extends FrameLayout implements BaseLockScreenView.onViewListener {
    private onLockScreenListener mListener;
    private boolean isPlay;
    private AudioManager mAudioManager;
    private SoundPool mPool;
    private int mResSoundPool;
    private boolean isHideView = false;

    public ScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenView(Context context) {
        this(context, null);
//        setBackgroundColor(Color.BLUE);
    }

    public void initView(BaseLockScreenView view) {
        removeAllViews();
        addView(view);
    }

    @Override
    public void addView(View child) {
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "addView : view == " + child);
        super.addView(child);
        if (child instanceof BaseLockScreenView) {
            ((BaseLockScreenView) child).setListener(this);
            if (child instanceof OtherLockScreenView) {
                startPlay();
            }
        }
        enterAnimation(child);
    }

    @Override
    public void addView(View child, int index) {
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "addView : view == " + child);
        super.addView(child, index);
        if (child instanceof BaseLockScreenView) {
            ((BaseLockScreenView) child).setListener(this);
            if (child instanceof OtherLockScreenView) {
                startPlay();
            }
        }
        enterAnimation(child);
    }

    public void addLockScreenView(BaseLockScreenView view) {
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "addLockScreenView : view == " + view);
        isHideView = false;
        if (getChildCount() == 0) {
            initView(view);
        } else if (view instanceof OtherLockScreenView) {
            return;
        } else {
            if (onInstanceof(LockScreenManager.OTHER_VIEW)) {
                int i = indexOfChild(getChild(LockScreenManager.OTHER_VIEW));
                if (i != -1) {
                    removeViewAt(i);
                }
            }

            if (onInstanceof(view)) {
                return;
            } else {
                if (view instanceof OtherLockScreenView) {
                    return;
                } else if (view instanceof CommonLockScreenView) {
                    if (onInstanceof(LockScreenManager.TIME_OUT_VIEW)) {
                        return;
                    } else if (onInstanceof(LockScreenManager.CHARGE_VIEW)) {
                        addView(view, 0);
                    } else if (onInstanceof(LockScreenManager.OTHER_VIEW)) {
                        removeAllViews();
                        addView(view);
                    } else {
                        addView(view);
                    }
                } else if (view instanceof TimeOutLockScreenView) {
                    if (onInstanceof(LockScreenManager.COMMON_VIEW) && onInstanceof(LockScreenManager.CHARGE_VIEW)) {
                        int i = indexOfChild(getChild(LockScreenManager.COMMON_VIEW));
                        removeViewAt(i);
                        addView(view, 0);
                    } else if (onInstanceof(LockScreenManager.COMMON_VIEW)) {
                        removeAllViews();
                        addView(view);
                    } else if (onInstanceof(LockScreenManager.CHARGE_VIEW)) {
                        addView(view, 0);
                    }
                } else if (view instanceof ChargeLockScreenView) {
                    if (onInstanceof(LockScreenManager.CHARGE_VIEW)) {
                        return;
                    } else {
                        addView(view);
                    }
                }
            }
        }
    }


    private void startPlay() {
        if (isPlay) return;
        isPlay = true;
        //add by wanghong for bug 50954 begin 20170109
        mAudioManager = (AudioManager) getContext().getSystemService(Service.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(OnAudioFocusChangeListener, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAudioManager.abandonAudioFocus(OnAudioFocusChangeListener);
            }
        }, 3000);
        //add by wanghong for bug 50954 end 20170109
        if (mPool != null) {
            mPool.release();
            mPool.stop(mResSoundPool);
        }
        mResSoundPool = initSoundPool();
        mPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1, 1, 0, 3, 1);
            }
        });
    }

    public void removeLockView(BaseLockScreenView view) {
        if (view instanceof OtherLockScreenView) {
            stopPlay();
        }
        exitAnimation(view);
    }

    public void setHideView(boolean ishideView) {
        isHideView = ishideView;
    }

    /**
     * add by wanghong for bug 50954   20170109
     */
    private AudioManager.OnAudioFocusChangeListener OnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    };

    private int initSoundPool() {
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            mPool = builder.build();
        } else {
            mPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        return mPool.load(getContext().getApplicationContext(), R.raw.doink, 1);
    }

    private void stopPlay() {
        isPlay = false;
        if (mPool != null) {
            //add by wanghong for bug 50954 begin 20170109
            if (mAudioManager != null)
                mAudioManager.abandonAudioFocus(OnAudioFocusChangeListener);
            //add by wanghong for end 50954 begin 20170109
            mPool.release();
            mPool.stop(mResSoundPool);
            mPool = null;
        }
    }

    public BaseLockScreenView getChild(int flag) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            boolean b = ((BaseLockScreenView) getChildAt(i)).equalsFlag(flag);
            if (b) {
                return (BaseLockScreenView) getChildAt(i);
            }
        }
        return null;
    }

    private boolean onInstanceof(BaseLockScreenView view) {
        boolean flag = false;

        if (view == null) {
            flag = true;
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            flag = view.equals(getChildAt(i));
            if (flag) {
                return flag;
            }
        }
        return flag;
    }

    public boolean onInstanceof(int view) {
        boolean flag = false;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            flag = ((BaseLockScreenView) getChildAt(i)).equalsFlag(view);
            if (flag) {
                return flag;
            }
        }
        return flag;
    }

    public void refreshView() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            enterAnimation(getChildAt(i));
        }
    }

    public void removeLockScreenView(int flag) {
        boolean b = onInstanceof(flag);
        if (flag == LockScreenManager.OTHER_VIEW) {
            stopPlay();
        }
        if (b) {
            int i = indexOfChild(getChild(flag));
            removeLockView((BaseLockScreenView) getChildAt(i));
        }
    }

    public void hideAllView() {
        isHideView = true;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            exitAnimation((BaseLockScreenView) getChildAt(i));
        }
    }

    @Override
    public void onHideView(BaseLockScreenView view) {
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onHideView: mListener == " + mListener);
        removeLockView(view);
        isHideView = true;
    }

    @Override
    public void onRemoveView(BaseLockScreenView view) {
        isHideView = false;
        removeLockView(view);
    }

    public void removeAllLockView() {
        isHideView = false;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            removeLockView((BaseLockScreenView) getChildAt(i));
        }
    }

    @Override
    public void closeApp() {
        KidsZoneApplication.closeApp();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                removeAllLockView();
            }
        }, 200);
    }

    public void setonLockScreenListener(onLockScreenListener listener) {
        this.mListener = listener;
    }

    public interface onLockScreenListener {
        void onHideView(BaseLockScreenView view);

        void onRemoveView();
    }

    private void enterAnimation(View child) {
        if (child instanceof CommonLockScreenView) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.screen_enter);
        child.startAnimation(animation);
    }

    private void exitAnimation(final BaseLockScreenView view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.screen_exit);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                if (!isHideView) removeView(mRemoveView);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        removeView(view);
                        LockScreenManager.getInstance(getContext()).removeCacheView(view);
                        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onAnimationEnd: isHideView == " + isHideView + "  getChildCount()  == " + getChildCount() + " mListener == " + mListener);
                        if (isHideView) {
                            if (mListener != null) mListener.onHideView(view);
                        } else if (!isHideView && getChildCount() == 0) {
                            if (mListener != null) mListener.onRemoveView();
                        }
                    }
                });
//                mRemoveView = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
        if (getChildCount() == 0 && mListener != null) {
            if (isHideView) {
                mListener.onHideView(view);
            } else {
                mListener.onRemoveView();
            }
        }
    }

}
