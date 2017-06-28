/*Top Secret*/
package com.dollyphin.kidszone.systemui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.Callbacks;
import com.dollyphin.kidszone.home.KidsZoneHome;
import com.dollyphin.kidszone.passwordview.PasswordView;
import com.dollyphin.kidszone.util.BlurBitmapUtil;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.view.KidsZoneDialogView;

import static com.dollyphin.kidszone.util.KidsZoneLog.KIDS_GUIDE_DEBUG;
import static com.dollyphin.kidszone.util.KidsZoneLog.KIDS_SYSTEMUI_DEBUG;

/**
 * Created by feng.shen on 2016/11/23.
 */

public class StatusBar extends FrameLayout implements StatusBarCallbacks {
    private ImageView mStatusBarBtn;
    private RelativeLayout mSystemUI;
    private KidsZoneDialogView mPassWordPanel;
    private Context mContext;
    private ImageView mBlurImg;
    private ImageView mCoverImg;
    private Bitmap mBgBitmap = null;
    private Bitmap mCoverBitmap = null;
    private static final int ALPHA_MAX_VALUE = 255;
    private int downY;
    private int currentLevel = 0;
    private int movePercent = 0;
    private KidsZoneHome.BlurBackgroundListener blurListener;
    private boolean isComplete = false;
    private int mStatusBarHeight;
    private int mScreenSizeWidth;
    private int mScreenSizeHeight;
    private LayoutInflater mLayoutInflater;

    private Expand mExpand;

    private TextView mExitKidsZone;
    private TextView mParentMode;
    private Callbacks mCallbacks;

    private PasswordView mPasswordView;
    private int mScreenHeight;
    private final int mScreenWidth;
    private int DURATION = 300;

    private boolean mExpandState = false;
    private boolean sUp = false;
    private int mMoveY = 0;
    private boolean mAnimated = false;

    private final int mPasswordTop;
    private final int mPasswordStart;
    private final int mPasswordEnd;
    private final int mPasswordBottom;
    private final int mStatusBtnWidth;

    enum State {NORMAL, FORGET}

    private State mState = State.NORMAL;

    public StatusBar(Context context) {
        this(context, null);
    }

    public StatusBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mStatusBarHeight = context.getResources().getDimensionPixelOffset(R.dimen.status_bar_btn_height);
        mLayoutInflater = LayoutInflater.from(context);
        mScreenSizeHeight = context.getResources().getDimensionPixelOffset(R.dimen.screen_height);
        mScreenSizeWidth = context.getResources().getDimensionPixelOffset(R.dimen.screen_width);
        mScreenHeight = mScreenSizeHeight;//KidsZoneUtil.getScreenHeight(context);
        mScreenWidth = mScreenSizeWidth;//KidsZoneUtil.getRealScreenWidth(context);
        mPasswordTop = context.getResources().getDimensionPixelOffset(R.dimen.system_ui_password_top);
        mPasswordStart = context.getResources().getDimensionPixelOffset(R.dimen.system_ui_password_start);
        mPasswordEnd = context.getResources().getDimensionPixelOffset(R.dimen.system_ui_password_end);
        mPasswordBottom = context.getResources().getDimensionPixelOffset(R.dimen.system_ui_password_bottom);
        mStatusBtnWidth = context.getResources().getDimensionPixelOffset(R.dimen.status_bar_btn_width);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        mStatusBarBtn = (ImageView) findViewById(R.id.status_bar_btn);

        mBlurImg = (ImageView) findViewById(R.id.kids_img);
        mCoverImg = (ImageView) findViewById(R.id.kids_img_for);

        mExpand = (Expand) findViewById(R.id.expand);

        mPassWordPanel = (KidsZoneDialogView) findViewById(R.id.password_status_bar);
        mPassWordPanel.setup(KidsZoneDialogView.Mode.SYSTEM_UI);
        mPassWordPanel.addCallback(this);
        mSystemUI = (RelativeLayout) findViewById(R.id.system_ui);

        mExitKidsZone = (TextView) findViewById(R.id.exit_kids);
        mParentMode = (TextView) findViewById(R.id.enter_parent);

        mPasswordView = (PasswordView) mLayoutInflater.inflate(R.layout.view_password, null);
        mPasswordView.setup(PasswordView.CHECK_VIEW, passWordComplete);
        mPassWordPanel.addView(mPasswordView);

        mExitKidsZone.setOnClickListener(onClick);
        mParentMode.setOnClickListener(onClick);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPasswordView.getLayoutParams();
        layoutParams.height = mScreenHeight;
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.setMargins(mPasswordStart, mPasswordTop, mPasswordEnd, mPasswordBottom);
        mPasswordView.setLayoutParams(layoutParams);
        initState();
    }

    public void setBlurListener(KidsZoneHome.BlurBackgroundListener listener) {
        blurListener = listener;
    }

    public void addCallback(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void setBlurBackgroud(Bitmap bitmap) {
        mCoverBitmap = Bitmap.createBitmap(bitmap);
        mCoverImg.setImageBitmap(mCoverBitmap);
        mBgBitmap = BlurBitmapUtil.blurBitmap(mContext, mCoverBitmap, 25f);
        mBlurImg.setImageBitmap(mBgBitmap);
        blurBackground();

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private void blurBackground() {
        mBlurImg.setVisibility(View.VISIBLE);
        mCoverImg.setVisibility(View.VISIBLE);
    }

    private OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.exit_kids:
                    if (mCallbacks != null) {
                        mCallbacks.closeApplication();
                    }
                    break;
                case R.id.enter_parent:
                    ComponentName componentName = new ComponentName("com.dollyphin.kidszone", "com.dollyphin.kidszone.parent.ParentalControlMode");
                    intent.setComponent(componentName);
                    mContext.startActivity(intent);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateOfSystemUI(0, -mScreenHeight, true);
                        }
                    }, 300);
                    break;
                default:
                    break;
            }
        }
    };

    private void initState() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.height = mStatusBarHeight;
        params.width = LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(params);

        mStatusBarBtn.setVisibility(View.VISIBLE);
        mPassWordPanel.setVisibility(GONE);
        mSystemUI.setVisibility(View.GONE);
        mCoverImg.setVisibility(View.GONE);
        mBlurImg.setVisibility(View.GONE);
        mExpandState = false;
        isComplete = false;
    }

    private void preMoveSystemUI() {
        mState = State.NORMAL;
        mStatusBarBtn.setVisibility(View.GONE);
        mSystemUI.setVisibility(GONE);
        setVisibility(GONE);
        //blurListener.init();
        //setBlurredLevel(40);
        setVisibility(VISIBLE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.height = mScreenHeight;
        params.width = mScreenWidth;
        setLayoutParams(params);

        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) mExpand.getLayoutParams();
        fl.height = mScreenHeight;
        fl.width = LayoutParams.MATCH_PARENT;
        fl.gravity = Gravity.BOTTOM;
        fl.bottomMargin = mScreenHeight;
        mExpand.setLayoutParams(fl);

        mPasswordView.setup(PasswordView.CHECK_VIEW);

        mPassWordPanel.setVisibility(VISIBLE);
        mPassWordPanel.showPassWord(null, false);
    }

    private void endSystemUI() {
        float translateY = Math.abs(mExpand.getTranslationY());
        if (sUp) {
            if (mExpandState) {
                animateOfSystemUI(-translateY, -mScreenHeight, true);
            } else {
                animateOfSystemUI(translateY, 0, true);
            }
        } else {
            if (mExpandState) {
                animateOfSystemUI(-translateY, 0, false);
            } else {
                animateOfSystemUI(translateY, mScreenHeight, false);
            }
        }
    }

    private void animateOfSystemUI(final float from, float to, final boolean up) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator translate;
        translate = ObjectAnimator.ofFloat(mExpand, "translationY", from, to);

        animatorSet.play(translate);
        animatorSet.setDuration(DURATION);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setBlurredLevel(!up ? 100 : 0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                KidsZoneLog.d(KIDS_GUIDE_DEBUG, "animation====>" + up);
                if (up) {
                    initState();
                    release();
                } else {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mExpand.getLayoutParams();
                    layoutParams.height = mScreenHeight;
                    layoutParams.bottomMargin = 0;
                    layoutParams.gravity = Gravity.BOTTOM;
                    mExpand.setTranslationY(0f);
                    mExpand.setLayoutParams(layoutParams);
                    mExpandState = true;
                    isComplete = true;
                }
                mAnimated = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimated = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
        mAnimated = true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        KidsZoneLog.d(KIDS_SYSTEMUI_DEBUG, "onTouchEvent ===>" + isComplete);
        if (mAnimated) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                KidsZoneLog.d(KIDS_SYSTEMUI_DEBUG, "onTouchEvent Down" + event.getY());
                downY = (int) event.getY();
                if (!isComplete) {
                    preMoveSystemUI();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                KidsZoneLog.d(KIDS_SYSTEMUI_DEBUG, "onTouchEvent Move" + event.getY());
                int moveY = (int) event.getY();
                int moveP = moveY - downY;
                if (moveY - mMoveY == 0) {
                    return false;
                }

                if (isComplete && moveP > 0) {
                    return false;
                }

                if (moveP == 0) {
                    return false;
                }

                sUp = moveY - mMoveY < 0;
                if (((mExpand.getTranslationY() > 0 && mExpandState) ||
                        (!mExpandState && mExpand.getTranslationY() > mScreenHeight)) && !sUp) {
                    return true;
                }
                mMoveY = moveY;
                isComplete = false;
                mExpand.setTranslationY(moveP);
                moveBlurLevel(moveY);
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                KidsZoneLog.d(KIDS_SYSTEMUI_DEBUG, "onTouchEvent Cancel" + event.getY());
                int upY = (int) event.getY();
                if (upY - downY == 0) {
                    if (!isComplete) {
                        mStatusBarBtn.setVisibility(GONE);
                        expand();
                    }
                    return true;
                }
                if (!sUp && isComplete) {
                    return true;
                }
                endSystemUI();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void moveBlurLevel(int move) {
        int screenY = mScreenHeight / 2;
        movePercent = move / screenY;
        currentLevel = (movePercent * 100);
        if (currentLevel < 40) {
            currentLevel = 40;
        }
        if (currentLevel > 100) {
            currentLevel = 100;
        }
        setBlurredLevel(currentLevel);
    }

    public void setBlurredLevel(int level) {
        if (level < 0 || level > 100) {
            throw new IllegalStateException("No validate level, the value must be 0~100");
        }
        KidsZoneLog.d(KIDS_SYSTEMUI_DEBUG, "onTouchEvent currentLevel" + currentLevel);
        mCoverImg.setImageAlpha((int) (ALPHA_MAX_VALUE - level * 2.55));
    }

    private void release() {
        if (mBgBitmap != null && !mBgBitmap.isRecycled()) {
            mBgBitmap.recycle();
            mBgBitmap = null;
        }

        if (mCoverBitmap != null && !mCoverBitmap.isRecycled()) {
            mCoverBitmap.recycle();
            mCoverBitmap = null;
        }
    }

    public void expand() {
        preMoveSystemUI();
        animateOfSystemUI(0, mScreenHeight, false);
    }

    public void collapse() {
        animateOfSystemUI(0, -mScreenHeight, true);
    }

    private PasswordView.PassWordComplete passWordComplete = new PasswordView.PassWordComplete() {
        @Override
        public void onComplete(boolean complete) {
            if (complete) {
                mPassWordPanel.setVisibility(GONE);
                mSystemUI.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onShowPwd(PasswordView view, Spanned s) {
            if (mPassWordPanel != null) {
                mPassWordPanel.showPassWord(s, true);
            }
            mState = State.FORGET;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mState == State.FORGET) {
                        mPassWordPanel.setVisibility(GONE);
                        mSystemUI.setVisibility(VISIBLE);
                    }
                }
            }, 2000);
        }
    };

    @Override
    public void exit() {
        animateOfSystemUI(0, -mScreenHeight, true);
    }

    @Override
    public void emergency() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.android.phone", "com.android.phone.EmergencyDialer");
        intent.setComponent(componentName);
        mContext.startActivity(intent);
        animateOfSystemUI(0, -mScreenHeight, true);
    }
}
