/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.Context;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class WhiteFilter extends Filter {
    private Context mContext;

    public WhiteFilter(Context context) {
        mContext = context;
    }

    @Override
    public boolean filter(String pname, String name) {
        return false;
    }
}
