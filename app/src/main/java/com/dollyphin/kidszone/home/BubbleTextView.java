/*Top Secret*/
package com.dollyphin.kidszone.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.AppInfo;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by feng.shen on 2016/12/1.
 */

public class BubbleTextView extends TextView {
    private Drawable mIcon;
    private int mIconSize = 0;
    private int mLeftIconSize = 0;
    private int mGuideIconSize = 0;

    public BubbleTextView(Context context) {
        this(context, null);
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.icon_size);
        mLeftIconSize = getContext().getResources().getDimensionPixelSize(R.dimen.app_manager_icon_size);
        mGuideIconSize = context.getResources().getDimensionPixelSize(R.dimen.guide_icon_size);
    }

    public void applyFromAppInfo(AppInfo info) {
        setText(info.getLabel());
        setIcon(info.getBadgeIcon());
        setTag(info);
    }

    public void applyGuideItemFromAppInfo(AppInfo info) {
        setText(info.getLabel());
        setIcon(info.getBadgeIcon());
        setTag(info);
    }

    public void applyFromAppInfoFilter(AppInfo info) {
        setText(info.getLabel());
        setLeftIcon(info.getIcon());
        setTag(info);
    }

    public void setIcon(Bitmap icon) {
        Drawable topDrawable = KidsZoneUtil.createIconDrawable(icon, mIconSize);
        setCompoundDrawables(null, topDrawable, null, null);
    }

    public void setGuideIcon(Drawable icon) {
        int size = icon.getIntrinsicHeight();
        if (size >= mGuideIconSize) {
            size = mGuideIconSize;
        }

        int top = (mGuideIconSize - size) / 2;
        setCompoundDrawablePadding(getContext().getResources().getDimensionPixelOffset(R.dimen.icon_drawable_padding));
        icon.setBounds(0, top, size, top + size);
        setCompoundDrawables(null, icon, null, null);
    }

    private void setLeftIcon(Drawable icon) {
        icon.setBounds(0, 0, mLeftIconSize, mLeftIconSize);
        setCompoundDrawables(icon, null, null, null);
    }
}
