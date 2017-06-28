/*Top Secret*/
package com.dollyphin.kidszone.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.dollyphin.kidszone.R;

import java.util.ArrayList;

/**
 * Created by feng.shen on 2016/12/2.
 */

public class PageIndicator extends LinearLayout {
    private LayoutInflater mLayoutInflater;
    private ArrayList<PageInditorMarker> mMarker = new ArrayList<>();

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void addAllMarker(int size, int index) {
        if (mMarker.size() != 0) {
            removeAllMarker();
        }
        for (int i = 0; i < size; i++) {
            PageInditorMarker marker = (PageInditorMarker) mLayoutInflater.inflate(R.layout.page_indicator_marker, null);
            if (i == index) {
                marker.setImageResource(R.drawable.ic_pageindicator_current);
            }
            addView(marker);
            mMarker.add(marker);
        }
    }

    public void updateMarker(int index) {
        int size = mMarker.size();
        removeAllMarker();
        addAllMarker(size, index);
    }

    public void removeAllMarker() {
        while (mMarker.size() > 0) {
            removeMarker(Integer.MAX_VALUE);
        }
    }

    public void removeMarker(int index) {
        if (mMarker.size() > 0) {
            index = Math.max(0, Math.min(mMarker.size() - 1, index));
            removeView(mMarker.get(index));
            mMarker.remove(index);
        }
    }
}
