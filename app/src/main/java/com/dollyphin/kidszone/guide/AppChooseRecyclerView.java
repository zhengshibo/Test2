/*Top Secret*/
package com.dollyphin.kidszone.guide;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.AppInfo;
import com.dollyphin.kidszone.app.AppProvider;
import com.dollyphin.kidszone.home.BubbleTextView;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.ArrayList;

/**
 * Created by feng.shen on 2016/12/6.
 */

public class AppChooseRecyclerView extends RecyclerView {
    private LayoutInflater mLayoutInflater;
    ArrayList<Drawable> mList = new ArrayList<>();
    ArrayList<CharSequence> mLabelList = new ArrayList<>();
    ArrayList<Boolean> mSelectList = new ArrayList<>();
    private GuideAppChooseView.OnChooseClick mListener;
    private Context mContext;
    private AppChooseAdapter mAdapter;

    public AppChooseRecyclerView(Context context) {
        this(context, null);
    }

    public AppChooseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppChooseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        sync();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void init(GuideAppChooseView.OnChooseClick listener, ArrayList<AppInfo> list) {
        mListener = listener;
        mAdapter = new AppChooseAdapter();
        copy(list);
        //setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        setLayoutManager(gridLayoutManager);
        addItemDecoration(new SpaceItemDecoration(37));
        setAdapter(mAdapter);
    }

    private void copy(ArrayList<AppInfo> list) {
        mList.clear();
        mLabelList.clear();
        mSelectList.clear();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AppInfo info = list.get(i);
            Drawable icon = info.getIcon();
            CharSequence label = info.getLabel();
            mList.add(icon);
            mLabelList.add(label);
            mSelectList.add(SharePrefereUtils.isDislpay(mContext, info.getAppName()));
        }
    }

    public void sync() {
        KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "==Guide sync==>" + AppProvider.data.size());
        copy(AppProvider.data);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    class AppChooseAdapter extends Adapter<AppChooseAdapter.AppChooseHolder> {

        @Override
        public AppChooseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AppChooseHolder(mLayoutInflater.inflate(R.layout.guide_choose_item, null));
        }

        @Override
        public void onBindViewHolder(final AppChooseHolder holder, final int position) {
            holder.bubbleTextView.setGuideIcon(mList.get(position));
            holder.bubbleTextView.setText(mLabelList.get(position));
            holder.mSelect.setVisibility(mSelectList.get(position) ? VISIBLE : GONE);
            holder.bubbleTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(holder.getLayoutPosition(), !mSelectList.get(holder.getLayoutPosition()));
                        mSelectList.set(holder.getLayoutPosition(), !mSelectList.get(holder.getLayoutPosition()));
                        notifyDataSetChanged();
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        class AppChooseHolder extends ViewHolder {
            private BubbleTextView bubbleTextView;
            private ImageView mSelect;

            public AppChooseHolder(View view) {
                super(view);
                bubbleTextView = (BubbleTextView) view.findViewById(R.id.guide_icon);
                mSelect = (ImageView) view.findViewById(R.id.guide_app_select);
            }
        }

    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, space, 0);
            if (parent.getChildPosition(view) == 0 || parent.getChildPosition(view) == 1) {
                outRect.left = 15;
            }
        }
    }
}
