/*Top Secret*/
package com.dollyphin.kidszone.guide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.Callbacks;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.view.KidsZoneDialogView;


/**
 * Created by feng.shen on 2016/12/6.
 */

public class Guide extends FrameLayout {
    private KidsZoneDialogView mCenterPager;
    private GuidePagerView mGuidePagerView;

    private Callbacks mCallbacks;

    public static final int GUIDE_NUMER = 5;
    public static final int GUIDE_DEFAULT = 0;

    private final int mGuideViewPagerWidth;
    private final int mGuideViewPagerHeight;
    private final int mGuideViewPagerTop;

    public Guide(Context context) {
        this(context, null);
    }

    public Guide(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Guide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGuideViewPagerWidth = context.getResources().getDimensionPixelOffset(R.dimen.guide_viewpager_width);
        mGuideViewPagerHeight = context.getResources().getDimensionPixelOffset(R.dimen.guide_viewpager_height);
        mGuideViewPagerTop = context.getResources().getDimensionPixelOffset(R.dimen.guide_viewpager_top);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void addCallbacks(Callbacks callback) {
        mCallbacks = callback;
    }

    public void viewStubGuide() {
        if (mGuidePagerView != null) {
            mGuidePagerView.vieStub();
        }
    }

    private void init() {
        mCenterPager = (KidsZoneDialogView) findViewById(R.id.pager_center);
        mCenterPager.setup(KidsZoneDialogView.Mode.GUIDE);
        mCenterPager.addCallback(mGuideCallbacks);

        mGuidePagerView = (GuidePagerView) findViewById(R.id.guide_pager);
        mGuidePagerView.addCallbacks(mGuideCallbacks);
        layout();
    }

    private void layout() {
        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) mGuidePagerView.getLayoutParams();
        fl.width = mGuideViewPagerWidth;
        fl.height = mGuideViewPagerHeight;
        fl.setMargins(0, mGuideViewPagerTop, 0, 0);
        fl.gravity = Gravity.CENTER_HORIZONTAL;
        mGuidePagerView.setLayoutParams(fl);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private GuideCallbacks mGuideCallbacks = new GuideCallbacks() {
        @Override
        public void change(int page) {
            KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "==change==>" + page);
            mGuidePagerView.setCurrentItem(page);
            if (page == GuidePanelControl.PASSWORD_MODE) {
                mGuidePagerView.initPassWord();
            }
        }

        @Override
        public void close() {
            if (mCallbacks != null) {
                mCallbacks.closeGuide();
            }

            if (mGuidePagerView != null) {
                mGuidePagerView.save();
            }
        }

        @Override
        public void exit() {
            if (mCallbacks != null) {
                mCallbacks.showExitDialog();
            }
        }

        @Override
        public void check() {
            mGuidePagerView.check();
        }

        @Override
        public void show(String date) {
            if (mCallbacks != null) {
                mCallbacks.showCalendar(date);
            }
        }

        @Override
        public void complete() {
            if (mCenterPager != null) {
                mCenterPager.completePassWord();
            }
        }

        @Override
        public void setNextEnable(boolean enable) {
            if (mCenterPager != null) {
                mCenterPager.setNextEnable(enable);
            }
        }
    };

    public void onResult(String date) {
        if (mGuidePagerView != null) {
            mGuidePagerView.updateBirth(date);
        }
    }

    public void sync() {
        if (mGuidePagerView != null) {
            mGuidePagerView.sync();
        }
    }
}
