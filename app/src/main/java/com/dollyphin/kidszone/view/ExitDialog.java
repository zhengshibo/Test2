/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by hong.wang on 2016/12/28.
 */
public class ExitDialog extends Dialog implements View.OnClickListener {

    private final View mContent;
    private Button mExitOk;
    private Button mExitCancel;
    private OnExitOkListener mListener;

    public ExitDialog(Context context) {
        this(context, R.style.dialogStyle);
    }

    public ExitDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContent = LayoutInflater.from(context).inflate(R.layout.dialog_exit, null);
        mExitOk = (Button) mContent.findViewById(R.id.exit_ok);
        mExitCancel = (Button) mContent.findViewById(R.id.exit_cancel);

        mExitOk.setOnClickListener(this);
        mExitCancel.setOnClickListener(this);
    }


    @Override
    public void show() {
        if (isShowing()) return;
        setContentView(mContent);
        KidsZoneUtil.hideNav(mContent);
        super.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_ok:
                if (mListener != null) mListener.onOkClick(v);
                break;
            case R.id.exit_cancel:
                break;
        }
        dismiss();
    }

    public void setOnExitOkListener(OnExitOkListener listener) {
        mListener = listener;
    }

    public interface OnExitOkListener {
        void onOkClick(View v);
    }
}
