/*Top Secret*/
package com.dollyphin.kidszone.parent;

import android.os.Bundle;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.application.BaseActivity;
import com.dollyphin.kidszone.passwordview.ForgetPassword;
import com.dollyphin.kidszone.passwordview.PasswordView;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.view.LimitTimeView;

public class BackgroundActivity<T> extends BaseActivity implements View.OnClickListener, PasswordView.PassWordComplete {
    public static final String SKIP_MARKER = "skip_marker";
    public static final int USE_OF_DAY = 1 << 5;
    public static final int TOP_FIVE = 1 << 6;
    public static final int CHANGE_PWD = 10000;
    public static final int AGREEMENT_TIME = 10001;
    public static final int WALLPAPER = 10002;
    public static final int ABOUT = 10003;

    private final String SETTING_CANCEL = "cancel";
    private final String SETTING_OK = "ok";

    private Button mSettingLeft;
    private View mSettingRight;
    private FrameLayout mContent;
    private View mView;
    private int mExtra;
    private TextView mViewShowPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_background, null);
        setContentView(view);
        view.setOnClickListener(this);
        mExtra = getIntent().getIntExtra(SKIP_MARKER, -1);
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onCreate: intExtra ==> " + mExtra);

        mContent = (FrameLayout) findViewById(R.id.setting_content);
        RelativeLayout mContentAdd = (RelativeLayout) findViewById(R.id.setting_content_add);
        mSettingLeft = (Button) findViewById(R.id.setting_left);
        mSettingRight = findViewById(R.id.setting_right);
        mViewShowPwd = (TextView) findViewById(R.id.parent_show_pwd);
        mViewShowPwd.setVisibility(View.GONE);
        mSettingLeft.setOnClickListener(this);
        mSettingRight.setOnClickListener(this);
        mContentAdd.setOnClickListener(this);
        mContent.setOnClickListener(this);
        mSettingLeft.setVisibility(View.GONE);

        mView = getTypeView(mExtra);
        RelativeLayout.LayoutParams params = getParam();
        int margin = (int) getResources().getDimension(R.dimen.limit_margin_start);
        if (mExtra == AGREEMENT_TIME) {
            mContentAdd.setBackground(getDrawable(R.drawable.guide_background));
            mSettingLeft.setVisibility(View.VISIBLE);
            params.topMargin = (int) getResources().getDimension(R.dimen.limit_margin_top);
            params.leftMargin = margin;
            params.rightMargin = margin;
        } else if (mExtra == CHANGE_PWD) {
            mContentAdd.setBackground(getDrawable(R.drawable.guide_background));
            params.topMargin = (int) getResources().getDimension(R.dimen.limit_chang_pwd_top);
            params.leftMargin = margin;
            params.rightMargin = margin;
        }

        if (mExtra == CHANGE_PWD) {
            params.leftMargin = (int) getResources().getDimension(R.dimen.change_pwd_margin_start);
            params.rightMargin = (int) getResources().getDimension(R.dimen.change_pwd_margin_end);
            params.bottomMargin = (int) getResources().getDimension(R.dimen.change_pwd_margin_buttom);
        }

        if (mExtra == USE_OF_DAY || mExtra == TOP_FIVE) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContentAdd.getLayoutParams();
            layoutParams.topMargin = (int) getResources().getDimension(R.dimen.dp_20);
            layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.dp_20);
            layoutParams.rightMargin = (int) getResources().getDimension(R.dimen.dp_20);
            mContentAdd.setLayoutParams(layoutParams);
        }
        mContentAdd.addView(mView == null ? new View(this) : mView, params);
    }

    private RelativeLayout.LayoutParams getParam() {
        return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                int realHeight = KidsZoneUtil.getRealHeight(BackgroundActivity.this);
                DisplayMetrics mDm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getRealMetrics(mDm);
                int realHeight = mDm.heightPixels;//add by zhengshibo 
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContent.getLayoutParams();
                int measuredHeight = mContent.getMeasuredHeight();
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onGlobalLayout:realHeight == " + realHeight + "  height == " + measuredHeight);
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                params.height = measuredHeight;
                params.topMargin = (realHeight - params.height) / 2;
                int margin = 0;
                int marginLeft;
                int marginRight;
                if (mExtra == USE_OF_DAY || mExtra == TOP_FIVE) {
                    marginLeft = (int) getResources().getDimension(R.dimen.top_five_padding);
                    marginRight = (int) getResources().getDimension(R.dimen.top_five_padding_right);
                    params.rightMargin = marginRight;
                    params.leftMargin = marginLeft;
                } else if (mExtra == CHANGE_PWD || mExtra == AGREEMENT_TIME) {
                    params.topMargin = 0;
                    params.height = KidsZoneUtil.getRealHeight(getApplicationContext());
                    margin = 0;
                    params.leftMargin = margin;
                    params.rightMargin = margin;
                } else {
                    margin = (int) getResources().getDimension(R.dimen.wallpaper_padding);
                    params.leftMargin = margin;
                    params.rightMargin = margin;
                }

                if (mExtra == CHANGE_PWD || mExtra == AGREEMENT_TIME) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSettingRight.getLayoutParams();
                    int btnMargin = (int) getResources().getDimension(R.dimen.exit_btn_margin);
                    layoutParams.topMargin = btnMargin;
                    layoutParams.rightMargin = btnMargin;
                }

                mContent.setLayoutParams(params);
                mContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private View getTypeView(int type) {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(this);
        switch (type) {
            case USE_OF_DAY:
                view = inflater.inflate(R.layout.view_stats_date, null);
                setRightVisible();
                break;
            case TOP_FIVE:
                view = inflater.inflate(R.layout.view_statscs, null);
                setRightVisible();
                break;
            case CHANGE_PWD:
                PasswordView inflate = (PasswordView) inflater.inflate(R.layout.view_password, null);
                inflate.setup(PasswordView.UPDATE_VIEW, this);
                view = inflate;
                setRightVisible();
                break;
            case AGREEMENT_TIME:
                view = inflater.inflate(R.layout.view_limit_time, null);
                setLeftVisible();
                setRightVisible();
                break;
            case WALLPAPER:
                view = inflater.inflate(R.layout.view_setting_wallpaer, null);
                break;
            case ABOUT:
                view = inflater.inflate(R.layout.view_about, null);
                break;
            default:
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "type Untreated");
                break;
        }
        return view;
    }

    private void setLeftVisible() {
        mSettingLeft.setVisibility(View.VISIBLE);
        mSettingLeft.setTag(SETTING_OK);
    }

    private void setRightVisible() {
        mSettingRight.setVisibility(View.VISIBLE);
        mSettingRight.setTag(SETTING_CANCEL);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setting_content_add)
            return;
        Object vTag = v.getTag();
        if (vTag instanceof String) {
            String tag = (String) vTag;
            if (SETTING_OK.equals(tag)) {
                onOkClick();
            }
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void onOkClick() {
        KidsZoneLog.e(KidsZoneLog.KIDS_CONTROL_DEBUG, "onOkClick:mExtra is " + mExtra + "  mView ==> " + mView);
        if (AGREEMENT_TIME == mExtra) {
            if (mView instanceof LimitTimeView) {
                ((LimitTimeView) mView).saveTime();
            }
        }
    }

    @Override
    public void onComplete(boolean complete) {
        finish();
    }

    @Override
    public void onShowPwd(final PasswordView v, Spanned s) {
        mViewShowPwd.setVisibility(View.VISIBLE);
        mViewShowPwd.setText(s);
        mViewShowPwd.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewShowPwd.setVisibility(View.GONE);
                v.setup(PasswordView.UPDATE_VIEW);
            }
        }, 2000);
    }
}
