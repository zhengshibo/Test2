/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by feng.shen on 2017/1/6.
 */

public class UserNameEditTextView extends EditText implements TextView.OnEditorActionListener {

    public interface BackKeyListener {
        boolean onBackKey();
    }

    private BackKeyListener mBackKeyListener;

    public UserNameEditTextView(Context context) {
        super(context);
    }

    public UserNameEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserNameEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addCallbacks(BackKeyListener listener) {
        this.setOnEditorActionListener(this);
        mBackKeyListener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            KidsZoneUtil.hideNavInKidsZone(getContext(), true);
            if (mBackKeyListener != null) {
                return mBackKeyListener.onBackKey();
            }
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * add by wanghong for bug 52370
     * @param textView
     * @param i
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        KidsZoneLog.d(KidsZoneLog.KIDS_SYSTEMUI_DEBUG, "onEditorAction: keyCode  == " + i);
        if (i == KeyEvent.KEYCODE_ENDCALL) {
            if (mBackKeyListener != null) mBackKeyListener.onBackKey();
            KidsZoneUtil.hideInputMethod(getContext(), this);
            KidsZoneUtil.hideNavInKidsZone(getContext(), true);
        }
        return false;
    }
}
