/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.lockscreen.ViewCheckItemAdapter;
import com.dollyphin.kidszone.util.KidsZoneLog;

/**
 * Created by hong.wang on 2016/12/14.
 */
public class AddUseTimeDialog extends LinearLayout implements ViewCheckItemAdapter.onItemClickListener, View.OnClickListener {

    private ListView mVAddUseTimeList;
    private Button mVAddUseTimeOk;
    private Button mVAddUseTimeCancel;
    private int mSelect = ViewCheckItemAdapter.DEFAULT_SELCLT;
    private AddUseTimeDialogListener mListener;
    private int[] mTime = new int[]{5, 15, 30, 60};

    public AddUseTimeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        removeAllViews();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVAddUseTimeList = (ListView) findViewById(R.id.v_add_use_time_list);
        mVAddUseTimeOk = (Button) findViewById(R.id.v_add_use_time_ok);
        mVAddUseTimeCancel = (Button) findViewById(R.id.v_add_use_time_cancel);
        mVAddUseTimeOk.setOnClickListener(this);
        mVAddUseTimeCancel.setOnClickListener(this);

        mVAddUseTimeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ViewCheckItemAdapter adapter = new ViewCheckItemAdapter(getContext(), getResources().getStringArray(R.array.add_use_time));
        adapter.setOnItemClickListener(this);
        mVAddUseTimeList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        KidsZoneLog.d(true, "onItemClick: position == " + position);
        mSelect = position;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            KidsZoneLog.e(KidsZoneLog.KIDS_LOCK_DEBUG, "onClick: mListener == " + mListener);
            return;
        }
        switch (v.getId()) {
            case R.id.v_add_use_time_ok:
                mListener.onOkClick(mTime[mSelect]);
                break;
            case R.id.v_add_use_time_cancel:
                mListener.onCancelClick();
                break;
        }
    }

    public void setListener(AddUseTimeDialogListener listener) {
        this.mListener = listener;
    }

    public interface AddUseTimeDialogListener {
        void onOkClick(int min);

        void onCancelClick();
    }
}
