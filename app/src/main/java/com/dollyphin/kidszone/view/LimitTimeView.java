/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.timemanager.TimeManager;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.SharePrefereUtils;

/**
 * Created by hong.wang on 2016/12/1.
 */
public class LimitTimeView extends LinearLayout implements RadioGroup.OnCheckedChangeListener {
    public static final int DEFAULT_USE_TIME = 40;
    public static final int DEFAULT_BREAK_TIME = 15;

    private RadioGroup mUseTimeView;
    private RadioGroup mBreakTimeView;
    private int mBreakTime;
    private int mUseTime;
    private Context mContext;
    private int mUnCheckcolor;
    private int mCheckedColor;


    public LimitTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mUnCheckcolor = getResources().getColor(R.color.green_txt);
        mCheckedColor = getResources().getColor(R.color.white);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUseTimeView = (RadioGroup) findViewById(R.id.use_time);
        mBreakTimeView = (RadioGroup) findViewById(R.id.break_time);

        mUseTimeView.setOnCheckedChangeListener(this);
        mBreakTimeView.setOnCheckedChangeListener(this);

        mUseTime = SharePrefereUtils.getUseTime(mContext);
        mBreakTime = SharePrefereUtils.getBreakTime(mContext);
        initUseTime(mUseTimeView, mUseTime);
        initBreakTime(mBreakTimeView, mBreakTime);
    }

    private void initUseTime(RadioGroup group, int time) {
        if (time == SharePrefereUtils.INVALID_INT) {
            time = DEFAULT_USE_TIME;
        }
        initGroupView(group, time);
    }


    private void initBreakTime(RadioGroup group, int time) {
        if (time == SharePrefereUtils.INVALID_INT) {
            time = DEFAULT_BREAK_TIME;
        }
        initGroupView(group, time);
    }

    private void initGroupView(RadioGroup group, int time) {
        try {
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                RadioButton child = (RadioButton) group.getChildAt(i);
                child.setChecked(false);
                String tag = (String) child.getTag();
                int childTime = Integer.valueOf(tag);
                if (time == childTime) {
                    KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "initGroupView: childTime ==> " + childTime + "  view id ==> " + child.getId());
                    child.setChecked(true);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onCheckedChanged : checkedId  ==> " + checkedId);
        switch (group.getId()) {
            case R.id.use_time:
                setUseTime(getRadioItemText(group, checkedId));
                break;
            case R.id.break_time:
                setBreakTime(getRadioItemText(group, checkedId));
                break;
            default:
                break;
        }
    }

    private void setUseTime(int time) {
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "setUseTime:time ==> " + time);
        mUseTime = time;
    }

    private void setBreakTime(int time) {
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "setBreakTime:time ==> " + time);
        mBreakTime = time;
    }

    public void saveTime() {
        SharePrefereUtils.saveUseTime(mContext, mUseTime);
        SharePrefereUtils.saveBreakTime(mContext, mBreakTime);
        TimeManager.getInstance(getContext()).updateTime();
    }

    private int getRadioItemText(RadioGroup group, int checkedId) {
        int time = 0;
        try {
            int count = group.getChildCount();
            RadioButton child;
            for (int i = 0; i < count; i++) {
                child = (RadioButton) group.getChildAt(i);
                child.setTextColor(mUnCheckcolor);
                if (child.getId() == checkedId) {
                    child.setTextColor(mCheckedColor);
                    String tag = (String) child.getTag();
                    time = Integer.valueOf(tag);
                    KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onCheckedChanged : text  ==> " + time);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

}


