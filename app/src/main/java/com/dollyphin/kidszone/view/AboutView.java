/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by hong.wang on 2016/12/7.
 */
public class AboutView extends LinearLayout {
    private Context mContext;

    public AboutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        TextView mViewVersionName = (TextView) findViewById(R.id.app_versionname);
        mViewVersionName.setText(KidsZoneUtil.getVersion(mContext));
    }
}
