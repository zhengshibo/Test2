/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.passwordview.PasswordView;
import com.dollyphin.kidszone.timemanager.TimeManager;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.SharePrefereUtils;

/**
 * Created by hong.wang on 2016/12/13.
 */
public class TimeOutLockScreenView extends BaseLockScreenView implements View.OnClickListener {
    private ImageView mVTimeOutImgPart;
    private Button mVTimeOutAddUseTime;
    private Button mVTimeOutEnterParentMode;
    private Button mVTimeOutSignOut;
    private RelativeLayout mVTimeOutPasswordSpace;
    private int mNext = 0;
    private final int ENTER_PARENT_MODE = 0;
    private final int ADD_USE_TIME = 1;
    private final int SIGN_OUT = 2;

    private int minute = 30;
    private int second = 0;
    private TextView mVBreakTime;
    private boolean finish = false;
    private TextView mViewShowPwd;
    private boolean isPasswordView;
    private ImageView mVPasswordExit;
    private View mPwdBgView;
    private ImageView mVTimeOutImgOrientationPortrait;


    public TimeOutLockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVTimeOutImgPart = (ImageView) findViewById(R.id.v_time_out_img_part);
        mVTimeOutImgOrientationPortrait = (ImageView) findViewById(R.id.v_tome_out_orientation_portrait);
        mVTimeOutAddUseTime = (Button) findViewById(R.id.v_time_out_add_use_time);
        mVTimeOutEnterParentMode = (Button) findViewById(R.id.v_time_out_enter_parent_mode);
        mVTimeOutSignOut = (Button) findViewById(R.id.v_time_out_sign_out);
        mVPasswordExit = (ImageView) findViewById(R.id.v_password_exit);//modify by wanghong for bug 52013
        mVTimeOutPasswordSpace = (RelativeLayout) findViewById(R.id.v_time_out_password_space);
        mPwdBgView = findViewById(R.id.v_pwd_bg);
        mViewShowPwd = (TextView) findViewById(R.id.lock_screen_parent_show_pwd);
        mViewShowPwd.setVisibility(GONE);
        mPwdBgView.setVisibility(GONE);

        mVBreakTime = (TextView) findViewById(R.id.v_add_use_time_break_time);
        mVTimeOutPasswordSpace.setOnClickListener(this);
        //modify by wanghong for bug 52013 begin
        mVPasswordExit.setVisibility(GONE);
        mVPasswordExit.setOnClickListener(this);
        //modify by wanghong for bug 52013 end

        addPasswordView(mVTimeOutPasswordSpace);

        mVTimeOutAddUseTime.setOnClickListener(this);
        mVTimeOutEnterParentMode.setOnClickListener(this);
        mVTimeOutSignOut.setOnClickListener(this);
        setPasswordViewVisible(false);
        setFinish(false);

        setMinute(SharePrefereUtils.getBreakTime(getContext()));
        setSecond(0);

        handler.post(run);
    }

    private void setTxt(String txt) {
        mVBreakTime.setText(getContext().getString(R.string.overplus_time, txt + "\t"));
    }

    private void setMinute(int minute) {
        this.minute = minute;
    }

    private void setSecond(int second) {
        this.second = second;
    }

    @Override
    protected void addPasswordView(@Nullable ViewGroup parentView) {
        isPasswordView = true;
        mVPasswordExit.setVisibility(VISIBLE);
        super.addPasswordView(parentView);
    }

    private void addSelectTimeView() {
        mVTimeOutPasswordSpace.removeAllViews();
        mVTimeOutPasswordSpace.setBackgroundColor(getResources().getColor(R.color.kids_system_ui_bg));
        AddUseTimeDialog addUseTimeDialog = (AddUseTimeDialog) LayoutInflater.from(getContext()).inflate(R.layout.view_add_use_time, null);
        LayoutParams params = new LayoutParams((int) getResources().getDimension(R.dimen.add_use_time_width), (int) getResources().getDimension(R.dimen.passwordview_height));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addUseTimeDialog.setLayoutParams(params);
        addUseTimeDialog.setListener(new AddUseTimeDialog.AddUseTimeDialogListener() {
            @Override
            public void onOkClick(int min) {
                setPasswordViewVisible(false);
                remove();
                setFinish(true);
                TimeManager.getInstance(getContext()).startWork(min);
            }

            @Override
            public void onCancelClick() {
                setPasswordViewVisible(false);
            }
        });
        mVTimeOutPasswordSpace.addView(addUseTimeDialog);
    }

    private void setPasswordViewVisible(boolean visible) {
        //modify by wanghong for bug 52013 begin
        isPasswordView = false;
        mVPasswordExit.setVisibility(GONE);
        //modify by wanghong for bug 52013 end
        mPwdBgView.setVisibility(visible ? VISIBLE : GONE);
        mVTimeOutPasswordSpace.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onClick: v == " + v);
        switch (v.getId()) {
            //modify by wanghong for bug 52013 begin
            case R.id.v_time_out_enter_parent_mode:
                mNext = ENTER_PARENT_MODE;
                setPasswordViewVisible(true);
                addPasswordView(mVTimeOutPasswordSpace);
                break;
            case R.id.v_time_out_add_use_time:
                mNext = ADD_USE_TIME;
                setPasswordViewVisible(true);
                addPasswordView(mVTimeOutPasswordSpace);
                break;
            case R.id.v_time_out_sign_out:
                mNext = SIGN_OUT;
                setPasswordViewVisible(true);
                addPasswordView(mVTimeOutPasswordSpace);
                break;
            case R.id.v_time_out_password_space:
                if (!isPasswordView)
                    setPasswordViewVisible(false);
                break;
            case R.id.v_password_exit:
                setPasswordViewVisible(false);
                break;
            //modify by wanghong for bug 52013 end
        }

    }


    private void next(int next) {
        switch (next) {
            case ENTER_PARENT_MODE:
                KidsZoneLog.d(true, "next: mListener == " + mListener);
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.dollyphin.kidszone", "com.dollyphin.kidszone.parent.ParentalControlMode");
                intent.setComponent(cn);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                SharePrefereUtils.setEnterParentMode(getContext(), true);
                getContext().startActivity(intent);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setPasswordViewVisible(false);
                        hide();
                    }
                }, 500);
                break;
            case ADD_USE_TIME:
                showSelectTimeDialog();
                break;
            case SIGN_OUT:
                setFinish(true);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setPasswordViewVisible(false);
                    }
                }, 700);
                if (mListener != null) mListener.closeApp();
                break;

        }
    }

    Handler handler = new Handler();

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (minute == 0) {
                if (second == 0) {
//                    hideLockScreen();
                    KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "Runnable: minute == " + minute + "second " + second);
                    setFinish(true);
                    remove();
                } else {
                    second--;
                    if (second >= 10) {
                        setTxt("0" + minute + ":" + second);
                    } else {
                        setTxt("0" + minute + ":0" + second);
                    }
                }
            } else {
                if (second == 0) {
                    second = 59;
                    minute--;
                    if (minute >= 10) {
                        setTxt(minute + ":" + second);
                    } else {
                        setTxt("0" + minute + ":" + second);
                    }

                    if (second == 59) {
//                        checkEnable();
                    }
                } else {
                    second--;
                    if (second >= 10) {
                        if (minute >= 10) {
                            setTxt(minute + ":" + second);
                        } else {
                            setTxt("0" + minute + ":" + second);
                        }
                    } else {
                        if (minute >= 10) {
                            setTxt(minute + ":0" + second);
                        } else {
                            setTxt("0" + minute + ":0" + second);
                        }
                    }
                }
            }
            if (!isFinish()) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private boolean isFinish() {
        return finish;
    }

    private void setFinish(boolean finish) {
        this.finish = finish;
    }

    private void showSelectTimeDialog() {
        addSelectTimeView();
        setPasswordViewVisible(true);
    }

    @Override
    public void onComplete(boolean complete) {
        if (!complete) {
            return;
        }
        next(mNext);
    }

    @Override
    public void onShowPwd(final PasswordView view, Spanned s) {
        mViewShowPwd.setVisibility(VISIBLE);
        mViewShowPwd.setText(s);
        mViewShowPwd.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewShowPwd.setVisibility(GONE);
                next(mNext);
            }
        }, 2000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ("P904".equals(getContext().getString(R.string.project))) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                mVTimeOutImgOrientationPortrait.setVisibility(VISIBLE);
            } else {
                mVTimeOutImgOrientationPortrait.setVisibility(GONE);
            }
        }
    }
}
