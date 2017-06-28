/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.parent.SettingItem;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hong.wang on 2016/11/29.
 */
public class SettingCommonItemView extends RelativeLayout implements View.OnClickListener {

    private final String RIGHT_ARROW = "right_arrow";
    private final String SELECTION_VALUE = "selection_value";
    private final String SWITCH = "switch";
    private final String PROGRESSBAR = "progressbar";
    private final String TIME_AGREEMENT = "time_agreement";
    private final String BRIGHTNESS = "brightness";
    private final String VOLUME = "volume";

    private TextView mSettingCommonLeft;
    private SettingItem mItem;
    private Context mContext;
    private RelativeLayout mSettingCommonRight;
    private SeekBar mProgressBar;
    private View mVUnderLine;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private PopupWindow mPopupWindow;
    private CompoundButton mDataCheckBtn;


    public SettingCommonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }


    public void setItem(SettingItem item) {
        if (item == null) {
            KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "setItem : item is null");
            return;
        }
        mItem = item;
        addRightView(item.getRightPart());
        mSettingCommonLeft.setText(KidsZoneUtil.getString(mContext, item.getLabel()));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSettingCommonLeft = (TextView) findViewById(R.id.setting_common_left);
        mSettingCommonRight = (RelativeLayout) findViewById(R.id.setting_common_right);
        mVUnderLine = findViewById(R.id.setting_common_underline);
        setOnClickListener(this);
    }

    private void addRightView(String type) {
        mSettingCommonRight.removeAllViews();
        if (TextUtils.isEmpty(type)) {
            KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "addRightView: type is null");
            return;
        }
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "addRightView:label == > " + KidsZoneUtil.getString(mContext, mItem.getLabel()) + "   RightPart ==>" + type + "  key ==> " + mItem.getValueKey());
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (RIGHT_ARROW.equals(type)) {
            inflater.inflate(R.layout.setting_item_right_arrow, mSettingCommonRight);
        } else if (SWITCH.equals(type)) {
            Switch mSwitch = (Switch) inflater.inflate(R.layout.setting_item_switch, null);
            initSwitchView(mSwitch, mItem);
            mSwitch.setMinWidth((int) getResources().getDimension(R.dimen.dp_80));
            mSwitch.setMinHeight((int) getResources().getDimension(R.dimen.dp_80));
            mSettingCommonRight.addView(mSwitch, getParams());
        } else if (SELECTION_VALUE.equals(type)) {
            TextView mViewTxt = (TextView) inflater.inflate(R.layout.view_setting_txt, null);
            initTextView(mViewTxt, mItem);
            mSettingCommonRight.addView(mViewTxt, getParams());
        } else if (PROGRESSBAR.equals(type)) {
            mProgressBar = (SeekBar) inflater.inflate(R.layout.setting_seekbar, null);
            initSeekBar(mProgressBar);
            mSettingCommonRight.addView(mProgressBar, getParams());

        }
    }

    private void initSeekBar(final SeekBar bar) {
        if (VOLUME.equals(mItem.getValueKey())) {
            bar.setMax(15);
            //modify by wanghong for bug 52374 begin
            SharedPreferences preferences = mContext.getSharedPreferences(SharePrefereUtils.SETTING_PARAM, Context.MODE_PRIVATE);
            if (onSharedPreferenceChangeListener != null) {
                preferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
            }
            onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                    if (SharePrefereUtils.SETTING_PARAM_PREF.KIDS_ZONE_VOLUME.equals(s)) {
                        bar.setProgress(SharePrefereUtils.getKidsZoneVolume(getContext()));
                    }
                }
            };
            preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
            //modify by wanghong for bug 52374 end
            bar.setProgress(KidsZoneUtil.getAudio(mContext));
            SharePrefereUtils.setKidsZoneVolume(mContext, KidsZoneUtil.getAudio(mContext));
        } else if (BRIGHTNESS.equals(mItem.getValueKey())) {
            bar.setMax(255);
            bar.setProgress(SharePrefereUtils.getKidsZoneBrightness(mContext));
        }

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                KidsZoneLog.d(true, "initSeekBar: progress == " + progress);
//                if (!seekBar.isPressed()) return; remove by wanghong for bug 52683
                if (VOLUME.equals(mItem.getValueKey())) {
                    KidsZoneUtil.setAudio(mContext, progress);
                    SharePrefereUtils.setKidsZoneVolume(mContext, progress);
                } else if (BRIGHTNESS.equals(mItem.getValueKey())) {
                    if (KidsZoneUtil.isAotuBrightnessState(mContext)) {
                        KidsZoneUtil.setBrightnessManual(mContext);
                    }
                    KidsZoneUtil.setBrightness(mContext, progress);
                    SharePrefereUtils.setKidsZoneBrightness(mContext, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void initTextView(TextView view, SettingItem item) {
        if (SharePrefereUtils.SETTING_PARAM_PREF.WIFI.equals(item.getValueKey())) {
            Drawable drawable = getResources().getDrawable(R.drawable.wifi_icon);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            view.setCompoundDrawables(drawable, null, null, null);
            view.setText(getWIFISSID(mContext));
        } else if (TIME_AGREEMENT.equals(item.getValueKey())) {
            view.setText(mContext.getString(R.string.setting_time, SharePrefereUtils.getUseTime(mContext), SharePrefereUtils.getBreakTime(mContext)));
        } else if (SharePrefereUtils.SETTING_PARAM_PREF.WAPPAPER.equals(item.getValueKey())) {
            view.setText(KidsZoneUtil.getWallpaperName(mContext, SharePrefereUtils.getWallpaper(mContext)));
        }
    }

    private void initSwitchView(Switch mSwitch, final SettingItem item) {
        if (SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA.equals(mItem.getValueKey())) {
            mSwitch.setChecked(SharePrefereUtils.getIntParam(mContext, item.getValueKey()) != SharePrefereUtils.INVALID_INT);
        } else if (SharePrefereUtils.SETTING_PARAM_PREF.IS_EYE_MODEL.equals(item.getValueKey())) {
            mSwitch.setChecked(SharePrefereUtils.isKidsZoneEyeMode(mContext));
        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed())
                    return;
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onCheckedChanged: isChecked ==> " + isChecked);
                if (SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA.equals(mItem.getValueKey())) {
                    if (!KidsZoneUtil.hasSimCard(getContext())) {
                        Toast.makeText(getContext(), getContext().getString(R.string.no_sim), Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                    } else if (!KidsZoneUtil.isAirplaneMode(getContext())) {
                        Toast.makeText(mContext, mContext.getString(R.string.airplane_mode), Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                    } else {
                        SubscriptionManager subscriptionManager = KidsZoneUtil.getSubscriptionManager(getContext());
                        if (subscriptionManager.getActiveSubscriptionInfoList().size() > 1) {
                            buttonView.setChecked(!isChecked);
                            showPopupWindow(buttonView);
                        } else {
                            KidsZoneUtil.setMobileDataStatus(mContext, isChecked);
                            SharePrefereUtils.setIntParam(mContext, item.getValueKey(), isChecked ? 1 : -1);
                        }
                    }
                } else if (SharePrefereUtils.SETTING_PARAM_PREF.IS_EYE_MODEL.equals(item.getValueKey())) {
                    SharePrefereUtils.setKidsZoneEyeMode(mContext, isChecked ? 1 : 0);
                    KidsZoneUtil.setEyeMode(mContext, isChecked ? 1 : 0);
                }
            }
        });
    }

    @NonNull
    private LayoutParams getParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_VERTICAL, TRUE);
        params.addRule(ALIGN_PARENT_END, TRUE);
        return params;
    }

    @Override
    public void onClick(View v) {
        if (mItem == null || mItem.getIntent() == null) {
            KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onClick : mItem ==> " + mItem);
            return;
        }
        mContext.startActivity(mItem.getIntent());
    }

    private String getWIFISSID(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        //modify by wanghong for bug 51058  begin 20170109
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            String s = info.getSSID();
            if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                return s.substring(1, s.length() - 1);
            }

        }
        return "";
        //modify by wanghong for bug 51058  end 20170109
    }

    public void setUnderlineVisible(boolean visible) {
        this.mVUnderLine.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private List<SubscriptionInfo> getSubscriptionInfos() {
        SubscriptionManager subscriptionManager = KidsZoneUtil.getSubscriptionManager(getContext());
        return subscriptionManager.getActiveSubscriptionInfoList();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showPopupWindow(CompoundButton view) {
        mDataCheckBtn = view;
        int popuWindowWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.popuwindow_width);
        int popuWindowHeight = getContext().getResources().getDimensionPixelOffset(R.dimen.popuwindow_height);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.popup_list_data, null);
        mPopupWindow = new PopupWindow(contentView, popuWindowWidth, popuWindowHeight);
        ListView listView = (ListView) contentView.findViewById(R.id.popup_list);
        List<String> strings = new ArrayList<>();
        strings.add(getContext().getString(R.string.mobile_data_close));
        List<SubscriptionInfo> subscriptionInfos = getSubscriptionInfos();

        if (subscriptionInfos != null)
            for (SubscriptionInfo info : subscriptionInfos) {
                strings.add((String) info.getDisplayName());
            }
        PopupDataListAdapter adapter = new PopupDataListAdapter(strings.toArray(new String[strings.size()]));
        listView.setAdapter(adapter);
        if (SharePrefereUtils.getIntParam(getContext(), SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA) == SharePrefereUtils.INVALID_INT) {
            adapter.setCheckPosition(0);
        } else {
            int i = 1;
            for (SubscriptionInfo info : subscriptionInfos) {
                if (info.getSubscriptionId() == SharePrefereUtils.getIntParam(getContext(), SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA)) {
                    adapter.setCheckPosition(i);
                }
                i++;
            }
        }
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(null);
        int[] windowPos = calculatePopWindowPos(this, contentView);
        if (!mPopupWindow.isShowing()) {
            //mPopupWindow.showAsDropDown(this, 0, 0, Gravity.END);
            mPopupWindow.showAtLocation(this, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        }
    }

    private int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        final int screenHeight = KidsZoneUtil.getScreenHeight(getContext());
        final int screenWidth = KidsZoneUtil.getScreenWidth(getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight - getHeight() * 3 / 4;
        }
        return windowPos;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void onChecked(int position) {
        KidsZoneLog.d(true, "onChecked: mDataCheckBtn == " + mDataCheckBtn);
        mPopupWindow.dismiss();
        List<SubscriptionInfo> subscriptionInfos = getSubscriptionInfos();
        if (position == 0) {
            for (SubscriptionInfo info : subscriptionInfos) {
                KidsZoneUtil.setMobileDataStatus(getContext(), info.getSubscriptionId(), false);
            }
            SharePrefereUtils.setIntParam(getContext(), SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA, SharePrefereUtils.INVALID_INT);
            if (mDataCheckBtn != null)
                mDataCheckBtn.setChecked(false);
        } else {
            for (SubscriptionInfo info : subscriptionInfos) {
                KidsZoneUtil.setMobileDataStatus(getContext(), info.getSubscriptionId(), false);
            }
            KidsZoneUtil.setMobileDataStatus(getContext(), subscriptionInfos.get(position - 1).getSubscriptionId(), true);
            SharePrefereUtils.setIntParam(getContext(), SharePrefereUtils.SETTING_PARAM_PREF.IS_OPEN_DATA, subscriptionInfos.get(position - 1).getSubscriptionId());
            if (mDataCheckBtn != null)
                mDataCheckBtn.setChecked(true);
        }
    }

    class PopupDataListAdapter extends BaseAdapter {
        private int checkPosition = -1;
        private String[] mData;

        public PopupDataListAdapter(String[] strings) {
            mData = strings;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void setCheckPosition(int position) {
            checkPosition = position;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int i, View view, final ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.view_check_item, viewGroup, false);
                view.setTag(new ViewHolder(view));
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.setPosition(i);
            if (checkPosition == i) {
                holder.checkboxItem.setChecked(true);
            } else {
                holder.checkboxItem.setChecked(false);
            }
            holder.checkboxTxt.setText(mData[i]);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolder viewHolder = (ViewHolder) view.getTag();
                    setCheckPosition(viewHolder.getPosition());
                    onChecked(viewHolder.getPosition());
                }
            });
            return view;
        }
    }

    public class ViewHolder {
        public CheckBox checkboxItem;
        public TextView checkboxTxt;
        private int position;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;

        }

        public ViewHolder(View view) {
            checkboxItem = (CheckBox) view.findViewById(R.id.checkbox_item);
            checkboxTxt = (TextView) view.findViewById(R.id.checkbox_txt);
        }
    }
}
