/*Top Secret*/
package com.dollyphin.kidszone.guide;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.passwordview.PasswordView;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.view.LimitTimeView;

import java.util.ArrayList;

/**
 * Created by feng.shen on 2016/12/5.
 */

public class GuidePagerView extends ViewPager {
    private Welcome mWelcome;
    private Information mInformation;
    private PasswordView mPasswordView;
    private GuideAppChooseView mGuideAppChooseView;
    private LimitTimeView mLimitTimeView;

    private LayoutInflater mLayoutInflater;
    private ArrayList<Object> mGuideView = new ArrayList<>();
    private GuideViewPagerAdapter mAdapter = new GuideViewPagerAdapter();
    private GuideCallbacks mCallbacks;

    public GuidePagerView(Context context) {
        this(context, null);
    }

    public GuidePagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
    }

    public void addCallbacks(GuideCallbacks callback) {
        mCallbacks = callback;
        setCurrentItem(Guide.GUIDE_DEFAULT);
    }

    private void initData() {
        mGuideView.clear();
        if (mInformation != null && mPasswordView != null && mGuideAppChooseView != null && mLimitTimeView != null) {
            return;
        }

        mWelcome = (Welcome) mLayoutInflater.inflate(R.layout.welcome, null);
        mInformation = (Information) mLayoutInflater.inflate(R.layout.information, null);
        mPasswordView = (PasswordView) mLayoutInflater.inflate(R.layout.view_password, null);
        mGuideAppChooseView = (GuideAppChooseView) mLayoutInflater.inflate(R.layout.guide_app_choose, null);
        mLimitTimeView = (LimitTimeView) mLayoutInflater.inflate(R.layout.view_limit_time, null);
        boolean setTextType = getContext().getResources().getBoolean(R.bool.welcome_text_type);
        if (setTextType) {
            Typeface typeface = KidsZoneUtil.getArialRoundedMtboldTypeface(getContext());
            KidsZoneUtil.setTypeface(typeface, mWelcome);
        }

        mInformation.setup(mInformationResult);
        mPasswordView.setup(mPasswordView.LOGIN_VIEW, passWordComplete);

        mGuideView.add(mWelcome);
        setAdapter(mAdapter);
    }

    public void vieStub() {
        mGuideView.add(mInformation);
        mGuideView.add(mPasswordView);
        mGuideView.add(mGuideAppChooseView);
        mGuideView.add(mLimitTimeView);

        mAdapter.notifyDataSetChanged();
    }

    class GuideViewPagerAdapter extends PagerAdapter {
        public GuideViewPagerAdapter() {
            KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "GuideViewPagerAdapter==>" + getCount());
        }

        @Override
        public int getCount() {
            return mGuideView.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView((View) mGuideView.get(position));
            return mGuideView.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) mGuideView.get(position));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public void check() {
        KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "==change==>" + mInformation);
        if (mInformation != null) {
            mInformation.check();
        }
    }

    public void sync() {
        if (mGuideAppChooseView != null) {
            mGuideAppChooseView.sync();
        }
    }

    public void updateBirth(String date) {
        if (mInformation != null) {
            mInformation.syncBirth(date);
        }
    }

    public void save() {
        if (mInformation != null) {
            mInformation.saveInformation();
        }

        if (mLimitTimeView != null) {
            mLimitTimeView.saveTime();
        }
    }

    public void initPassWord() {
        if (mPasswordView != null) {
            mPasswordView.initView();
        }
    }

    private PasswordView.PassWordComplete passWordComplete = new PasswordView.PassWordComplete() {
        @Override
        public void onComplete(boolean complete) {
            if (mCallbacks != null && complete) {
                setCurrentItem(GuidePanelControl.APP_MODE);
                mCallbacks.complete();
            }
        }

        @Override
        public void onShowPwd(PasswordView view, Spanned s) {

        }
    };

    private Information.InformationResult mInformationResult = new Information.InformationResult() {
        @Override
        public void show(String date) {
            mCallbacks.show(date);
        }

        @Override
        public void empty(boolean empty) {
            if (getCurrentItem() == GuidePanelControl.INFORMATION_MODE) {
                mCallbacks.setNextEnable(!empty);
            }
        }
    };
}
