/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.passwordview.PasswordView;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by hong.wang on 2016/12/16.
 */
public class ChargeLockScreenView extends BaseLockScreenView implements View.OnClickListener {


    public boolean isFullcharge = false;
    private Handler mHandler = new Handler();
    private AnimationDrawable mChargAnimation;
    private int durtion;
    private ImageView mChargeView;
    private TextView mHintView;
    private TextView mBatteryPctView;
    private TextView mExitChargingView;
    private RelativeLayout mChargePwdView;
    private ImageView mChargeExitPwdView;
    private TextView mChargeShowPwd;
    private View mPwdBgView;
    private ImageView mChargeOrientationPortraitView;

    public ChargeLockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackground(getResources().getDrawable(R.drawable.lock_screen_bg));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChargeView = (ImageView) findViewById(R.id.v_charge_lock_content);
        mChargeOrientationPortraitView = (ImageView) findViewById(R.id.v_charging_orientation_portrait);
        mBatteryPctView = (TextView) findViewById(R.id.v_charge_lock_battery_pct);
        mHintView = (TextView) findViewById(R.id.v_charge_lock_hint);
        mExitChargingView = (TextView) findViewById(R.id.v_exit_charging);
        mPwdBgView = findViewById(R.id.v_pwd_bg);
        mChargePwdView = (RelativeLayout) findViewById(R.id.v_charge_password_space);
        mChargeExitPwdView = (ImageView) findViewById(R.id.v_password_exit);
        mChargeShowPwd = (TextView) findViewById(R.id.lock_screen_parent_show_pwd);
        mChargeExitPwdView.setOnClickListener(this);
        if ("P904".equals(getContext().getString(R.string.project))) {
            //Set the font for the TextView begin
            Typeface typeface = KidsZoneUtil.getArialRoundedMtboldTypeface(getContext());
            KidsZoneUtil.setTypeface(typeface, mBatteryPctView, mHintView, mExitChargingView);
            //Set the font for the TextView end
        }
        ShowPwdView(false);
        mHintView.setText(getContext().getString(R.string.charge_hint));

        if ("P904".equals(getContext().getString(R.string.project))) {//if project is P904
            mChargAnimation = (AnimationDrawable) mChargeView.getDrawable();
            mChargAnimation.start();
            durtion = 0;
            for (int i = 0; i <= mChargAnimation.getNumberOfFrames(); i++) {
                durtion = mChargAnimation.getDuration(i);
            }
            mExitChargingView.setVisibility(VISIBLE);
            mBatteryPctView.setVisibility(VISIBLE);
            mExitChargingView.setOnClickListener(this);
            mHandler.postDelayed(runnable, durtion);
        } else {//if project is P701 or P702
            mExitChargingView.setVisibility(GONE);
            mBatteryPctView.setVisibility(GONE);
            mChargeView.setImageResource(R.drawable.charging_5);
        }
    }

    private void ShowPwdView(boolean isShow) {
        if (isShow) {
            mPwdBgView.setVisibility(VISIBLE);
            mChargePwdView.setVisibility(VISIBLE);
            addPasswordView(mChargePwdView);
            mChargeExitPwdView.setVisibility(VISIBLE);
        } else {
            mPwdBgView.setVisibility(GONE);
            mChargePwdView.setVisibility(GONE);
            mChargeExitPwdView.setVisibility(GONE);
            mChargeShowPwd.setVisibility(GONE);
        }
    }

    public void setFullcharge(boolean chargeStatus) {
        isFullcharge = chargeStatus;
    }

    public void setElectricity(int batteryPct) {
        mBatteryPctView.setText(getContext().getString(R.string.charging, String.valueOf(batteryPct)));
    }

    public boolean isFullcharge() {
        return isFullcharge;
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (isFullcharge()) {
                mBatteryPctView.setText(getContext().getString(R.string.charging, String.valueOf(100)));
                mChargeView.setImageResource(R.drawable.charging_5);
                mHintView.setText(getContext().getString(R.string.charge_full_hint));
            } else {
                if (!mChargAnimation.isRunning()) {
                    mChargAnimation.start();
                }
                mHandler.postDelayed(runnable, durtion);
            }

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_exit_charging:
                ShowPwdView(true);
                break;
            case R.id.v_password_exit:
                ShowPwdView(false);
                break;
            default:
                KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onClick: v --> " + view);
                break;
        }
    }

    @Override
    public void onComplete(boolean complete) {
        if (complete) remove();
    }

    @Override
    public void onShowPwd(PasswordView view, Spanned s) {
        mChargeShowPwd.setVisibility(VISIBLE);
        mChargeShowPwd.setText(s);
        mChargeShowPwd.postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowPwdView(false);
                remove();
            }
        }, 2000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ("P904".equals(getContext().getString(R.string.project))) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                mChargeOrientationPortraitView.setVisibility(VISIBLE);
            }else {
                mChargeOrientationPortraitView.setVisibility(GONE);
            }
        }
    }
}
