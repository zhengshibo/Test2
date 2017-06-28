/*Top Secret*/
package com.dollyphin.kidszone.guide;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.app.AppLoader;
import com.dollyphin.kidszone.app.AppProvider;
import com.dollyphin.kidszone.util.KidsZoneLog;

/**
 * Created by feng.shen on 2016/12/6.
 */

public class GuideAppChooseView extends RelativeLayout {
    private TextView mTitle;
    private Context mContext;
    private AppChooseRecyclerView mAppChooseRecyclerView;
    private AppLoader mLoader;

    interface OnChooseClick {
        void onClick(int pos, boolean display);
    }

    public GuideAppChooseView(Context context) {
        this(context, null);
    }

    public GuideAppChooseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideAppChooseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLoader = AppLoader.getInstance(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.choose_title);
        mTitle.setText(getAppNum(0, 0));

        mAppChooseRecyclerView = (AppChooseRecyclerView) findViewById(R.id.app_choose_recyclerview);
        mAppChooseRecyclerView.init(onClick, AppProvider.data);
    }

    private Spanned getAppNum(int choose, int num) {
        String format = mContext.getResources().getString(R.string.guide_choose_format);
        return Html.fromHtml(String.format(format, choose, num));
    }

    private OnChooseClick onClick = new OnChooseClick() {
        @Override
        public void onClick(int pos, boolean display) {
            KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "OnChooseClick==>" + AppProvider.data.get(pos).getLabel() + "==display==" + display);
            mLoader.updateDisplayApp(AppProvider.data.get(pos).getAppName(), display);
            mTitle.setText(getAppNum(AppProvider.display.size(), AppProvider.size()));
        }
    };

    public void sync() {
        if (mAppChooseRecyclerView != null) {
            mAppChooseRecyclerView.sync();
        }
        mTitle.setText(getAppNum(AppProvider.display.size(), AppProvider.size()));
    }

}
