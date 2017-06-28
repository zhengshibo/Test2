/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.application.BaseActivity;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

import java.util.ArrayList;
import java.util.List;

public class AppFilter extends BaseActivity implements View.OnClickListener, AppFilterCallback {

    private ImageView mAppFilterTitleBar;
    private RecyclerView mAppFilterRecycler;
    private AppLoader mLoader;
    private List<AppInfo> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_filter);
        mItems.clear();
        mLoader = AppLoader.getInstance(getApplicationContext());
        //init content
        mAppFilterTitleBar = (ImageView) findViewById(R.id.app_filter_title_bar);
        mAppFilterRecycler = (RecyclerView) findViewById(R.id.app_filter_recycler);

        mLoader.registerAppFilterCallBack(this);
        mAppFilterTitleBar.setOnClickListener(this);
        mAppFilterRecycler.setLayoutManager(new LinearLayoutManager(this));
        mItems.addAll(AppProvider.data);
        mAppFilterRecycler.setAdapter(new AppFilterAdapter(this, mItems));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_filter_title_bar:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoader.sync();
        if (mAppFilterRecycler != null) {
            mAppFilterRecycler.stopScroll();
        }
        mLoader.unregisterAppFilterCallBack();
    }

    @Override
    public void addApp(int position, AppInfo info) {
        mItems.add(position, info);
        mAppFilterRecycler.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void removeApp(AppInfo info) {
        int remove = mItems.indexOf(info);
        mItems.remove(info);
        mAppFilterRecycler.getAdapter().notifyItemRemoved(remove);
        mAppFilterRecycler.getAdapter().notifyDataSetChanged();
    }
}
