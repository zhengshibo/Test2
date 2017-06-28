/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.guide.GuideCallbacks;
import com.dollyphin.kidszone.guide.GuidePanelControl;
import com.dollyphin.kidszone.systemui.StatusBarCallbacks;
import com.dollyphin.kidszone.util.KidsZoneLog;

/**
 * Created by feng.shen on 2016/12/12.
 */

public class KidsZoneDialogView extends RelativeLayout implements DialogCallbacks {
    public enum Mode {SYSTEM_UI, GUIDE}

    private Mode mCurrentMode = Mode.GUIDE;
    private LayoutInflater mLayoutInflater;
    private ControlPanelButton mBack;
    private ControlPanelButton mExit;
    private ControlPanelButton mNext;
    private GuidePanelControl mGuideControl;
    private GuideCallbacks mGuideCallback;
    private ControlPanelButton mEmergency;
    private TextView mPassWordShow;
    private StatusBarCallbacks mStatusBarCallback;

    private final int exit = View.generateViewId();
    private final int back = View.generateViewId();
    private final int next = View.generateViewId();
    private final int emergency = View.generateViewId();
    private final int password = View.generateViewId();

    private final String EXIT = "exit";
    private final String BACK = "back";
    private final String NEXT = "next";
    private final String EMERGENCY = "emergency";
    private final String PASSWORD = "password";

    private final int mControlBottom;
    private final int mControlStartOrEnd;
    private final int mExitTop;
    private final int mExitEnd;
    private final int mEmergencyBottom;
    private final int mEmergencyEnd;
    private final int mPassWordTop;
    private final int mPassWordStart;

    public KidsZoneDialogView(Context context) {
        this(context, null);
    }

    public KidsZoneDialogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KidsZoneDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutInflater = LayoutInflater.from(context);
        mControlBottom = context.getResources().getDimensionPixelOffset(R.dimen.control_bottom);
        mControlStartOrEnd = context.getResources().getDimensionPixelOffset(R.dimen.control_start_end);
        mExitTop = context.getResources().getDimensionPixelOffset(R.dimen.exit_top);
        mExitEnd = context.getResources().getDimensionPixelOffset(R.dimen.exit_end);
        mEmergencyBottom = context.getResources().getDimensionPixelOffset(R.dimen.emergency_bottom);
        mEmergencyEnd = context.getResources().getDimensionPixelOffset(R.dimen.emerygency_end);
        mPassWordTop = context.getResources().getDimensionPixelOffset(R.dimen.password_show_top);
        mPassWordStart = context.getResources().getDimensionPixelOffset(R.dimen.password_show_start);
        setBackgroundResource(R.drawable.guide_background);
    }

    public void addCallback(GuideCallbacks callback) {
        mGuideCallback = callback;
    }

    public void addCallback(StatusBarCallbacks callback) {
        mStatusBarCallback = callback;
    }

    public void setup(Mode mode) {
        mCurrentMode = mode;
        initView();
    }

    private void initView() {
        switch (mCurrentMode) {
            case SYSTEM_UI:
                addItemToSystemUI();
                break;
            case GUIDE:
                addItemToGuide();
                break;
        }
        initClickListener();
    }

    private void addItemToGuide() {
        //add GuideControl
        mGuideControl = new GuidePanelControl(this);

        //add exit button
        inflaterExit();
        //add ControlPanel
        inflaterGuideControl();
    }

    private void addItemToSystemUI() {
        //add exit button
        inflaterExit();

        inflaterSystemUIControl();
    }

    private void inflaterGuideControl() {
        mBack = (ControlPanelButton) mLayoutInflater.inflate(R.layout.control_panel_button, null);
        mBack.setImageResource(R.drawable.guide_back);
        mBack.setId(back);
        mBack.setTag(BACK);
        mBack.setEnabled(false);
        mBack.setVisibility(INVISIBLE);
        RelativeLayout.LayoutParams backlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        backlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        backlp.setMargins(mControlStartOrEnd, 0, 0, mControlBottom);
        mBack.setLayoutParams(backlp);

        mNext = (ControlPanelButton) mLayoutInflater.inflate(R.layout.control_panel_button, null);
        mNext.setImageResource(R.drawable.guide_next);
        mNext.setId(next);
        mNext.setTag(NEXT);
        mNext.setEnabled(true);
        RelativeLayout.LayoutParams nextlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        nextlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        nextlp.addRule(RelativeLayout.ALIGN_PARENT_END);
        nextlp.setMargins(0, 0, mControlStartOrEnd, mControlBottom);
        mNext.setLayoutParams(nextlp);
        addView(mBack);
        addView(mNext);
    }

    private void inflaterSystemUIControl() {
        mEmergency = (ControlPanelButton) mLayoutInflater.inflate(R.layout.control_panel_button, null);
        mEmergency.setImageResource(R.drawable.emergency_dialer);
        mEmergency.setId(emergency);
        mEmergency.setTag(EMERGENCY);
        RelativeLayout.LayoutParams elp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        elp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        elp.addRule(RelativeLayout.ALIGN_PARENT_END);
        elp.setMargins(0, 0, mEmergencyEnd, mEmergencyBottom);
        mEmergency.setLayoutParams(elp);
        addView(mEmergency);

        int showPwdTextsize = getContext().getResources().getDimensionPixelOffset(R.dimen.showpwdtextsize);
        mPassWordShow = (TextView) mLayoutInflater.inflate(R.layout.control_text, null);
        mPassWordShow.setBackgroundResource(R.drawable.txt_bg);
        mPassWordShow.setId(password);
        mPassWordShow.setTag(PASSWORD);
        mPassWordShow.setVisibility(GONE);
        mPassWordShow.setTextSize(showPwdTextsize);
        mPassWordShow.setGravity(Gravity.CENTER);
        mPassWordShow.setTextColor(getResources().getColor(R.color.pwd_show_textcolor));

        RelativeLayout.LayoutParams plp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        plp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        plp.addRule(RelativeLayout.ALIGN_PARENT_START);
        plp.setMargins(mPassWordStart, mPassWordTop, 0, 0);
        mPassWordShow.setLayoutParams(plp);
        addView(mPassWordShow);
    }

    public void showPassWord(Spanned password, boolean show) {
        if (mPassWordShow != null) {
            mPassWordShow.setVisibility(show ? VISIBLE : GONE);
            mPassWordShow.setText(password);
        }
    }

    private void inflaterExit() {
        mExit = (ControlPanelButton) mLayoutInflater.inflate(R.layout.control_panel_button, null);
        mExit.setImageResource(R.drawable.guide_exit);
        mExit.setId(exit);
        mExit.setTag(EXIT);
        RelativeLayout.LayoutParams exittlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        exittlp.addRule(RelativeLayout.ALIGN_PARENT_END);
        exittlp.setMargins(0, mExitTop, mExitEnd, 0);
        mExit.setLayoutParams(exittlp);
        addView(mExit);
    }

    private void initClickListener() {
        if (mExit != null) {
            mExit.setOnClickListener(onClick);
        }
        if (mBack != null) {
            mBack.setOnClickListener(onClick);
        }
        if (mNext != null) {
            mNext.setOnClickListener(onClick);
        }
        if (mEmergency != null) {
            mEmergency.setOnClickListener(onClick);
        }
    }

    private OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = v.getTag().toString();
            switch (tag) {
                case EXIT:
                    if (mCurrentMode == Mode.GUIDE) {
                        if (mGuideControl != null) {
                            mGuideControl.exit();
                        }
                    } else {
                        if (mStatusBarCallback != null) {
                            mStatusBarCallback.exit();
                        }
                    }
                    break;
                case BACK:
                    if (mGuideControl != null) {
                        mGuideControl.back();
                    }
                    break;
                case NEXT:
                    if (mGuideControl != null) {
                        mGuideControl.next();
                    }
                case EMERGENCY:
                    if (mStatusBarCallback != null) {
                        mStatusBarCallback.emergency();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void completePassWord() {
        setMode(GuidePanelControl.APP_MODE);
    }

    public void setNextEnable(boolean enable) {
        mNext.setEnabled(enable);
    }

    private void setMode(int mode) {
        if (mGuideControl != null) {
            mGuideControl.setMode(mode);
        }
    }

    @Override
    public void setEnable(boolean back, boolean next) {
        KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "setEnable==>" + back + "==next==>" + next);
        mBack.setEnabled(back);
        mBack.setVisibility(back?VISIBLE:INVISIBLE);
        mNext.setEnabled(next);
    }

    @Override
    public void updateNext(boolean next) {
        mNext.setImageResource(next ? R.drawable.guide_next : R.drawable.guide_ok);
    }

    @Override
    public void change(int page) {
        mGuideCallback.change(page);
    }

    @Override
    public void close() {
        mGuideCallback.close();
    }

    @Override
    public void closeApplication() {
        mGuideCallback.exit();
    }


    @Override
    public void check() {
        mGuideCallback.check();
    }
}
