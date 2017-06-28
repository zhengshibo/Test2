/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.view.View;

import com.dollyphin.kidszone.guide.Guide;

import java.util.ArrayList;

/**
 * Created by feng.shen on 2016/12/17.
 */

public interface Callbacks {
    void bindScreen(int size);

    void bindItem(ArrayList<AppInfo> infos, boolean current);

    void bindGuide();

    void closeApplication();

    void closeGuide();

    void showCalendar(String date);

    void clear();

    boolean onLongClick(View v);

    void onPageSelected(int pos);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void showExitDialog();
}
