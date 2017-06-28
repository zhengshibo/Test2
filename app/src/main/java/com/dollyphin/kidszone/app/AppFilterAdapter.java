/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.home.BubbleTextView;
import com.dollyphin.kidszone.util.KidsZoneLog;

import java.util.List;

/**
 * Created by hong.wang on 2016/12/4.
 */
public class AppFilterAdapter extends RecyclerView.Adapter<AppFilterAdapter.AppItemViewHolder> {
    private Context mContext;
    private List<AppInfo> mInfos;
    private AppLoader mLoader;

    public AppFilterAdapter(Context context, List<AppInfo> infos) {
        mContext = context;
        mInfos = infos;
        mLoader = AppLoader.getInstance(context);
    }


    @Override
    public AppItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.app_filter_item, null);
        return new AppItemViewHolder(inflate);
    }

    @Override
    public int getItemCount() {
        if (mInfos == null && mInfos.size() == 0) {
            return 0;
        } else {
            int size = mInfos.size();
            for (int i = size - 1; i >= 0; i--) {
                AppInfo Info = mInfos.get(i);
                if ((!TextUtils.isEmpty(Info.getLabel())) && Info.getLabel().contains(Info.getPackageName())) {
                    mInfos.remove(i);
                }
            }
            return mInfos.size();
        }
//        return mInfos == null ? 0 : mInfos.size();
    }

    @Override
    public void onBindViewHolder(AppItemViewHolder holder, int position) {
        holder.setItem(mInfos.get(position));
    }


    public class AppItemViewHolder extends RecyclerView.ViewHolder {
        private BubbleTextView mAppContent;
        private Switch mAppLimitSwitch;


        public AppItemViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View root) {
            mAppContent = (BubbleTextView) root.findViewById(R.id.app_content);
            mAppLimitSwitch = (Switch) root.findViewById(R.id.app_limit_switch);
        }

        public void setItem(final AppInfo info) {
            mAppContent.applyFromAppInfoFilter(info);
            KidsZoneLog.d(KidsZoneLog.KIDS_APP_DEBUG, "setItem ==> " + AppProvider.display.contains(info));
            mAppLimitSwitch.setOnCheckedChangeListener(null);
            mAppLimitSwitch.setChecked(AppProvider.display.contains(info));
            mAppLimitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!buttonView.isPressed()) return;
                    mLoader.updateDisplayApp(info.getAppName(), isChecked);
                }
            });
        }

    }

}

