/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.Context;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class AllAppList {
    private BlackFilter mBlackFilter;
    private LimitFilter mLimitFilter;
    private Context mContext;

    public AllAppList(Context context, BlackFilter bfilter, LimitFilter lfilter) {
        mContext = context;
        mBlackFilter = bfilter;
        mLimitFilter = lfilter;
    }

    public void add(AppInfo info) {
        if (mBlackFilter != null && mBlackFilter.filter(info.getPackageName(), info.getClassName())) {
            return;
        }
        AppProvider.updateAllApp(info);

        if (mLimitFilter != null && mLimitFilter.filter(info.getPackageName(), info.getClassName())) {
            return;
        }
        AppProvider.updateDisplayApp(info);
    }

    public void sort(Context context) {
        AppProvider.sort(context);
    }


    public void clear() {
        AppProvider.clear();
    }

    public int size() {
        return AppProvider.display.size();
    }

    public AppInfo getApp(String app){
       return AppProvider.getApp(app);
    }

    public int getIndex(int index){
        return AppProvider.getIndex(index);
    }
}
