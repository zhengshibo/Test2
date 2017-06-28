/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.Context;

import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class LimitFilter extends Filter {
    private Context mContext;

    public LimitFilter(Context context) {
        mContext = context;
    }

    @Override
    public boolean filter(String pname, String name) {
        return !SharePrefereUtils.isDislpay(mContext, KidsZoneUtil.getApp(pname, name));
    }

    public static boolean display(Context context, String pname, String name) {
        return SharePrefereUtils.isDislpay(context, KidsZoneUtil.getApp(pname, name));
    }
}
