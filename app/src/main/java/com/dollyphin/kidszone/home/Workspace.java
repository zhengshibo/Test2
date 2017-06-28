/*Top Secret*/
package com.dollyphin.kidszone.home;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.Callbacks;
import com.dollyphin.kidszone.util.KidsZoneLog;

import java.util.ArrayList;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class Workspace extends ViewPager {
    private WorkspaceAdapter mAdapter;
    private Callbacks mCallback;
    ArrayList<CellLayout> mWorkspaceScreens = new ArrayList<>();
    private LayoutInflater mInflater;

    public Workspace(Context context) {
        this(context, null);
    }

    public Workspace(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
    }

    public void init(Callbacks callback, int size) {
        mCallback = callback;
        mWorkspaceScreens.clear();
        for (int i = 0; i < size; i++) {
            CellLayout cellLayout = (CellLayout) mInflater.inflate(R.layout.workspace_screen, null);
            cellLayout.setOnLongClickListener(onLongClick);
            mWorkspaceScreens.add(cellLayout);
        }
        mAdapter = new WorkspaceAdapter(mWorkspaceScreens);
        setAdapter(mAdapter);
        addOnPageChangeListener(pageChange);
    }

    public int getChildsize() {
        int size = 0;
        for (CellLayout cellLayout : mWorkspaceScreens) {
            size += cellLayout.getChildCount();
        }
        return size;
    }

    public int size() {
        return mWorkspaceScreens.size();
    }

    public void addItemToScreen(BubbleTextView app, int page) {
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "addItemToScreen==>" + page);
        if (mWorkspaceScreens.size() == 0) {
            return;
        }
        CellLayout cellLayout = mWorkspaceScreens.get(page);
        cellLayout.addView(app);
    }

    public void sync() {
        KidsZoneLog.d(KidsZoneLog.KIDS_MAIN_DEBUG, "sync==>" + mWorkspaceScreens.size());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void clear() {
        for (CellLayout cellLayout : mWorkspaceScreens) {
            cellLayout.removeAllViews();
        }
    }

    private OnLongClickListener onLongClick = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mCallback != null) {
                mCallback.onLongClick(v);
            }
            return false;
        }
    };

    private OnPageChangeListener pageChange = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mCallback != null) {
                mCallback.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

        }

        @Override
        public void onPageSelected(int position) {
            if (mCallback != null) {
                mCallback.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getCurrentItem() == 0 && getChildCount() == 0) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getCurrentItem() == 0 && getChildCount() == 0) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
