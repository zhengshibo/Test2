/*Top Secret*/
package com.dollyphin.kidszone.parent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.view.SettingCommonItemView;

import java.util.List;

/**
 * Created by hong.wang on 2016/11/29.
 */
public class SettingAdapter extends BaseAdapter {
    private final int ITEM_TYPE_NUM = 2;
    private final int TYPE_MODEL = 1;
    private final int TYPE_COMMON = 0;
    private List<SettingItem> mItems;
    private Context mContext;

    public SettingAdapter(Context context, List<SettingItem> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPE_NUM;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getIsModel();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonSettingItemHolder mCommonHolder = null;
        ModelSettingItemHolder mModelHolder = null;
        if (convertView == null) {
            if (TYPE_MODEL == getItemViewType(position)) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.setting_item_model, null);
                mModelHolder = new ModelSettingItemHolder();
                mModelHolder.initView(convertView);
                convertView.setTag(mModelHolder);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.setting_item_common, null);
                mCommonHolder = new CommonSettingItemHolder();
                mCommonHolder.initView(convertView);
                convertView.setTag(mCommonHolder);
            }
        } else {
            if (TYPE_MODEL == getItemViewType(position)) {
                mModelHolder = (ModelSettingItemHolder) convertView.getTag();
            } else {
                mCommonHolder = (CommonSettingItemHolder) convertView.getTag();
            }
        }

        if (TYPE_MODEL == getItemViewType(position)) {
            mModelHolder.mSettingModelMiddle.setText(KidsZoneUtil.getString(mContext, mItems.get(position).getLabel()));
        } else {
            mCommonHolder.mRoot.setItem(mItems.get(position));
            if (position + 1 < mItems.size()) {
                mCommonHolder.mRoot.setUnderlineVisible(TYPE_COMMON == getItemViewType(position + 1));
            }
        }

        return convertView;
    }

    private class CommonSettingItemHolder {
        SettingCommonItemView mRoot;
        TextView mSettingCommonLeft;

        private void initView(View root) {
            KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "initView : root ==> " + root);
            if (root instanceof SettingCommonItemView) {
                mRoot = (SettingCommonItemView) root;
            }
            mSettingCommonLeft = (TextView) root.findViewById(R.id.setting_common_left);
        }

    }

    private class ModelSettingItemHolder {
        TextView mSettingModelMiddle;

        private void initView(View root) {
            mSettingModelMiddle = (TextView) root.findViewById(R.id.setting_model_middle);
        }

    }
}
