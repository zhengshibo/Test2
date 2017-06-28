/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.lockscreen.LockScreenManager;
import com.dollyphin.kidszone.passwordview.PasswordView;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by hong.wang on 2016/12/13.
 */
public class BaseLockScreenView extends RelativeLayout implements PasswordView.PassWordComplete {
    protected int mflag = LockScreenManager.COMMON_VIEW;
    protected onViewListener mListener;

    public BaseLockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackground(getResources().getDrawable(R.drawable.lock_screen_bg));
    }

    public int getFlag() {
        return mflag;
    }

    public void setFlag(int flag) {
        this.mflag = flag;
    }

    @Override
    protected void onFinishInflate() {
        KidsZoneUtil.hideNav(this);
        super.onFinishInflate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        KidsZoneUtil.hideNav(this);
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseLockScreenView) {
            return mflag == ((BaseLockScreenView) obj).getFlag();
        }
        return false;
    }

    public boolean equalsFlag(int flag) {
        return mflag == flag;
    }

    public void hide() {
        if (mListener != null) mListener.onHideView(this);
    }

    public void remove() {
        if (mListener != null) {
            mListener.onRemoveView(this);
            mListener = null;
        }
    }

    protected void addPasswordView(@Nullable ViewGroup parentView) {
        parentView.removeAllViews();
        PasswordView mVTimeOutPassword = (PasswordView) LayoutInflater.from(getContext()).inflate(R.layout.view_password, parentView, false);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.add_use_time_height));
        params.rightMargin = (int) getResources().getDimension(R.dimen.screen_pwd_right);
        params.leftMargin = (int) getResources().getDimension(R.dimen.time_out_password_margin_start);
        params.topMargin = (int) getResources().getDimension(R.dimen.time_out_pwd_margin_top);
        mVTimeOutPassword.setLayoutParams(params);
        parentView.setBackground(getResources().getDrawable(R.drawable.time_out_pwd_bg));
        parentView.addView(mVTimeOutPassword);
        mVTimeOutPassword.setup(PasswordView.CHECK_VIEW, this);
    }

    public void setListener(onViewListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onComplete(boolean complete) {
        //Subclasses implement specific content
    }

    @Override
    public void onShowPwd(PasswordView view, Spanned s) {
        //Subclasses implement specific content
    }

    public interface onViewListener {
        void onHideView(BaseLockScreenView view);

        void onRemoveView(BaseLockScreenView view);

        void closeApp();
    }

    public void onConfigurationChanged(Configuration newConfig) {

    }


}
