/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.Context;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class BlackFilter extends Filter {
    private Context mContext;
    private List<String> appFilter;

    public BlackFilter(Context context) {
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        appFilter = Arrays.asList(context.getResources().getStringArray(R.array.black_filter));
    }

    @Override
    public boolean filter(String pname, String name) {
        return appFilter.contains(KidsZoneUtil.getApp(pname, name));
    }
}
