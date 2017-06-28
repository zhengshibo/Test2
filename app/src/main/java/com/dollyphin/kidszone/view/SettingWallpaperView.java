/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

/**
 * Created by hong.wang on 2016/12/5.
 */
public class SettingWallpaperView extends RelativeLayout implements onItemClickListener {
    private Context mContext;
    private final int[] mImgs = new int[]{R.drawable.setting_preview_wallpaper_0, R.drawable.setting_preview_wallpaper_1,
            R.drawable.setting_preview_wallpaper_2, R.drawable.setting_preview_wallpaper_3,
            R.drawable.setting_preview_wallpaper_4, R.drawable.setting_preview_wallpaper_5,
            R.drawable.setting_preview_wallpaper_6, R.drawable.setting_preview_wallpaper_7,
            R.drawable.setting_preview_wallpaper_8, R.drawable.setting_preview_wallpaper_9,
            R.drawable.setting_preview_wallpaper_10, R.drawable.setting_preview_wallpaper_11};
    private int mSelect;
    private PreviewWallpaperAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private final int ITEM_SPACE = 32;
    private LinearLayoutManager mManager;

    public SettingWallpaperView(Context context) {
        this(context, null);
    }

    public SettingWallpaperView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingWallpaperView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mSelect = SharePrefereUtils.getWallpaper(mContext);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRecyclerView = (RecyclerView) findViewById(R.id.wallpaper_content);
        mManager = new LinearLayoutManager(getContext());
        mManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mManager);
        mAdapter = new PreviewWallpaperAdapter(mContext);
        mAdapter.setonItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ITEM_SPACE));
    }

    public void saveWallpaper() {
        if (mAdapter != null) {
            SharePrefereUtils.setWallpaper(mContext, mSelect);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position == mManager.findFirstVisibleItemPosition()) {
            int left = mRecyclerView.getChildAt(0).getLeft();
            if (left < ITEM_SPACE) {
                mRecyclerView.smoothScrollBy(-(ITEM_SPACE - left), 0);
            }
        } else if (position == mManager.findLastVisibleItemPosition()) {
            int childCount = mManager.getChildCount();
            int right = mRecyclerView.getChildAt(childCount - 1).getRight();
            int moveX = mRecyclerView.getRight() - right;
            if (moveX < ITEM_SPACE) {
                mRecyclerView.smoothScrollBy(ITEM_SPACE - moveX, 0);
            }
        }
    }

    public class PreviewWallpaperAdapter extends RecyclerView.Adapter<PreviewWallpaperViewHolder> {
        private Context mContext;
        private onItemClickListener mListener;

        public PreviewWallpaperAdapter(Context context) {
            mContext = context;
        }

        @Override
        public PreviewWallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.setting_wallpaper_item, parent, false);
            return new PreviewWallpaperViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(PreviewWallpaperViewHolder holder, int position) {
            holder.mSettingWallpaperImg.setImageDrawable(mContext.getDrawable(mImgs[position]));
            holder.setPosition(position);
            holder.setChecked(position == mSelect);
            holder.setOnClick();
            holder.setonItemClickListener(mListener);
        }

        @Override
        public int getItemCount() {
            return mImgs.length;
        }

        public void setonItemClickListener(onItemClickListener listener) {
            this.mListener = listener;
        }

    }


    public class PreviewWallpaperViewHolder extends RecyclerView.ViewHolder {

        private View mRoot;
        private ImageView mSettingWallpaperImg;
        private TextView mSettingWallpaperName;
        private View mSettingWallpaperChecked;
        private int mPosition = 0;
        private onItemClickListener mListener;

        public PreviewWallpaperViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView;
            mSettingWallpaperImg = (ImageView) itemView.findViewById(R.id.setting_wallpaper_img);
            mSettingWallpaperName = (TextView) itemView.findViewById(R.id.setting_wallpaper_name);
            mSettingWallpaperChecked = itemView.findViewById(R.id.setting_wallpaper_checked);
        }

        public void setonItemClickListener(onItemClickListener listener) {
            this.mListener = listener;
        }

        private void setPosition(int position) {
            mSettingWallpaperName.setText(KidsZoneUtil.getWallpaperName(mContext, position));
            mPosition = position;
        }

        public void setChecked(boolean isCheck) {
            mSettingWallpaperChecked.setVisibility(isCheck ? VISIBLE : INVISIBLE);
        }

        private void setOnClick() {
            mRoot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelect = mPosition;
                    SharePrefereUtils.setWallpaper(mContext, mSelect);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    if (mListener != null) mListener.onItemClick(mRoot, mPosition);
                }
            });
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = space;
            }
            outRect.right = space;
        }
    }
}

interface onItemClickListener {
    void onItemClick(View view, int position);
}
