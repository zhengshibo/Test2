/*Top Secret*/
package com.dollyphin.kidszone.home;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.dollyphin.kidszone.util.KidsZoneLog;

import java.util.ArrayList;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class WorkspaceAdapter extends PagerAdapter {
    private ArrayList<CellLayout> mList;

    public WorkspaceAdapter(ArrayList<CellLayout> list) {
        initList(list);
    }

    private void initList(ArrayList<CellLayout> list) {
        if (mList == null) {
            mList = new ArrayList<>(list.size());
        }

        if (mList.size() != 0) {
            mList.clear();
        }
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "initList==>" + list.size());
        for (CellLayout cellLayout : list) {
            mList.add(cellLayout);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CellLayout cellLayout = mList.get(position);
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "instantiateItem==>" + cellLayout.getChildCount());
        container.addView(mList.get(position));
        return mList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mList.get(position));
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
