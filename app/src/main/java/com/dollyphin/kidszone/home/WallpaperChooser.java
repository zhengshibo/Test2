/*Top Secret*/
package com.dollyphin.kidszone.home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.ArrayList;

/**
 * Created by feng.shen on 2016/12/14.
 */

public class WallpaperChooser extends ViewPager {
    public static final int WALLPAPER_NUM = 12;
    public static final String CHOOSER_WALLPAPER = "setting_chooser_wallpaper_";

    private ArrayList<ImageView> mList = new ArrayList<>();
    private LayoutInflater mInflater;
    private ChooserAdapter mAdapter = new ChooserAdapter();
    private WallpaperChooseListener mListener;

    private float mOtherScale = getResources().getDimension(R.dimen.home_wallpaper_other);

    private int downX = 0;
    private int downY = 0;

    private int mCurrentPos = 0;
    private Context mContext;

    public interface WallpaperChooseListener {
        void end(int pos);
    }

    public WallpaperChooser(Context context) {
        this(context, null);
    }

    public WallpaperChooser(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void setup(WallpaperChooseListener listener) {
        mListener = listener;
        mCurrentPos = SharePrefereUtils.getWallpaper(mContext);
        setAdapter(mAdapter);
        setPageMargin(-25);
        setOffscreenPageLimit(3);
        setCurrentItem(mCurrentPos);
        initScale(mCurrentPos);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0) {
                    return;
                }
                mList.get(position).setScaleX(mOtherScale + (1 - positionOffset) * (1 - mOtherScale));
                mList.get(position).setScaleY(mOtherScale + (1 - positionOffset) * (1 - mOtherScale));

                if (position + 1 < mList.size()) {
                    mList.get(position + 1).setScaleX(1f - (1 - positionOffset) * (1 - mOtherScale));
                    mList.get(position + 1).setScaleY(1f - (1 - positionOffset) * (1 - mOtherScale));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initScale(int pos) {
        mList.get(pos).setScaleX(1f);
        mList.get(pos).setScaleY(1f);
    }

    private void init() {
        mList.clear();
        for (int i = 0; i < WALLPAPER_NUM; i++) {
            ImageView item = (ImageView) mInflater.inflate(R.layout.wallpaper_choose_item, null);
            item.setImageResource(getWallpaperResource(i));
            mList.add(item);
        }
    }

    private int getWallpaperResource(int index) {
        return KidsZoneUtil.getDrawableResource(mContext, CHOOSER_WALLPAPER + index);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean dispatchTouchEventWallpaper(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) ev.getX();
                int upY = (int) ev.getY();
                if (upX == downX && upY == downY) {
                    int result = viewOfClickOnScreen(ev);
                    if (result == -2) {
                        if (mListener != null) {
                            mListener.end(mCurrentPos);
                        }
                    } else if (result == 0) {
                        if (mListener != null) {
                            mListener.end(getCurrentItem());
                        }
                    } else {
                        int index = getCurrentItem() + result;
                        setCurrentItem(index);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * @param ev
     * @return
     */
    private int viewOfClickOnScreen(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

//        if (x < 171 && y > 125 && y < 592) {
//            return -1;
//        }
//
//        if (x > 1042 && y > 125 && y < 592) {
//            return 1;
//        }
//
//        if (x > 212 && x < 1000 && y > 125 && y < 592) {
//            return 0;
//        }
        int mLeftX = mContext.getResources().getDimensionPixelOffset(R.dimen.wallpaper_left_x);
        int mOtherUpY = mContext.getResources().getDimensionPixelOffset(R.dimen.wallpaper_other_up_y);
        int mOtherButtomY = mContext.getResources().getDimensionPixelOffset(R.dimen.wallpaper_other_buttom_y);
        int mMainLeftX = mContext.getResources().getDimensionPixelOffset(R.dimen.wallpaper_main_left_x);
        int mMainLeftY = mContext.getResources().getDimensionPixelOffset(R.dimen.wallpaper_main_left_y);
        int mMainRightX = mContext.getResources().getDimensionPixelOffset(R.dimen.wallpaper_main_right_x);
        int mMainRightY = mContext.getResources().getDimensionPixelOffset(R.dimen.wallpaper_main_right_y);
        int mRightX = mContext.getResources().getDimensionPixelOffset(R.dimen.wallpaper_right_x);

        if (x < mLeftX && y > mOtherUpY && y < mOtherButtomY) {
            return -1;
        }

        if (x > mRightX && y > mOtherUpY && y < mOtherButtomY) {
            return 1;
        }

        if (x > mMainLeftX && x < mMainRightX && y > mMainLeftY && y < mMainRightY) {
            return 0;
        }

        return -2;
    }

    class ChooserAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mList.get(position));
            if (position != getCurrentItem()) {
                mList.get(position).setScaleY(mOtherScale);
                mList.get(position).setScaleX(mOtherScale);
            }
            return mList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }
    }
}
