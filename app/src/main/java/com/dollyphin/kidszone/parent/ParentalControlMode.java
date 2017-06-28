/*Top Secret*/
package com.dollyphin.kidszone.parent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.application.BaseActivity;
import com.dollyphin.kidszone.application.KidsZoneApplication;
import com.dollyphin.kidszone.lockscreen.LockScreenManager;
import com.dollyphin.kidszone.util.ChooseUserPhoto;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;
import com.dollyphin.kidszone.view.ExitDialog;
import com.dollyphin.kidszone.view.UserNameEditTextView;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ParentalControlMode extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private ImageView mSettingBack;
    private ImageButton mKidsZoneExit;
    private ListView mSettingList;
    private TextView mUserName;
    private TextView mBirthday;
    private Button mUseOfDay;
    private Button mTopFive;
    private TextView mModelPerson;
    private ImageView mUserImg;
    private List<SettingItem> mItems;
    private SettingAdapter mAdapter;
    private UserNameEditTextView mEditUserName;
    private final int INPUT_NAME_MAX = 50;
    private boolean mExtra;
    private int mOldUseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parental_control_mode);
        initData();
    }

    private void initData() {
        mSettingBack = (ImageView) findViewById(R.id.setting_back);
        mKidsZoneExit = (ImageButton) findViewById(R.id.kids_zone_exit);
        mSettingList = (ListView) findViewById(R.id.setting_list);
        mSettingBack.setOnClickListener(this);
        mKidsZoneExit.setOnClickListener(this);

        //add list header
        View mViewHeader = LayoutInflater.from(this).inflate(R.layout.setting_header, null);
        mSettingList.addHeaderView(mViewHeader);

        mModelPerson = (TextView) mViewHeader.findViewById(R.id.setting_model_middle);
        mUserImg = (ImageView) mViewHeader.findViewById(R.id.setting_user_img);
        mUserName = (TextView) mViewHeader.findViewById(R.id.user_name);
        // modify by wanghong for bug 52370 begin
        mEditUserName = (UserNameEditTextView) mViewHeader.findViewById(R.id.edit_user_name);
        mEditUserName.addCallbacks(new UserNameEditTextView.BackKeyListener() {
            @Override
            public boolean onBackKey() {
                onFocusChange(mEditUserName, false);
                return false;
            }
        });
        // modify by wanghong for bug 52370 begin
        mEditUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(INPUT_NAME_MAX)});
        mBirthday = (TextView) mViewHeader.findViewById(R.id.birthday);
        mUseOfDay = (Button) mViewHeader.findViewById(R.id.use_of_day);
        mTopFive = (Button) mViewHeader.findViewById(R.id.top_five);

        //init header
        mUserName.setText(SharePrefereUtils.getUserName(this));
        mEditUserName.setVisibility(View.GONE);
        mModelPerson.setText(getString(R.string.person_model));
        mUserImg.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mBirthday.setOnClickListener(this);
        mTopFive.setOnClickListener(this);
        mUseOfDay.setOnClickListener(this);
        String userBirthday = SharePrefereUtils.getUserBirthday(this);
        if (TextUtils.isEmpty(userBirthday)) {
            userBirthday = KidsZoneUtil.Date2String(new Date());
            SharePrefereUtils.saveUserBirthday(this, userBirthday);
        }
        mBirthday.setText(userBirthday);
        if (KidsZoneUtil.getHeadImg() != null) {
            mUserImg.setImageBitmap(KidsZoneUtil.createBitmap(getApplicationContext(), KidsZoneUtil.getHeadImg()));
        }

        //init parent manager& system setting
        mItems = InitSettingInfo.readSettingXml(this);
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "initData ==> " + mItems.toString());
        mAdapter = new SettingAdapter(this, mItems);
        mSettingList.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mExtra = SharePrefereUtils.isEnterParentMode(this);
        KidsZoneLog.d(KidsZoneLog.KIDS_LOCK_DEBUG, "onWindowFocusChanged: mExtra == " + mExtra);
        //modify by wanghong for bug 49979 begin
        if (mExtra) {
            mOldUseTime = SharePrefereUtils.getUseTime(this);
        }
        //modify by wanghong for bug 49979 end
        SharePrefereUtils.setEnterParentMode(this, false);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.setting_back:
                //modify by wanghong for bug 49979 begin
                if (mOldUseTime < SharePrefereUtils.getUseTime(this)) {
                    LockScreenManager instance = LockScreenManager.getInstance(this);
                    instance.removeAllView();
                } else {
                    LockScreenManager.getInstance(this).showView();
                }
                mExtra = false;
                //modify by wanghong for bug 49979 end
                SharePrefereUtils.setEnterParentMode(this, false);
                finish();
                break;
            case R.id.kids_zone_exit:
                showCloseDialog();
                break;
            case R.id.use_of_day:
                intent = new Intent(this, BackgroundActivity.class);
                intent.putExtra(BackgroundActivity.SKIP_MARKER, BackgroundActivity.USE_OF_DAY);
                startActivity(intent);
                break;
            case R.id.top_five:
                intent = new Intent(this, BackgroundActivity.class);
                intent.putExtra(BackgroundActivity.SKIP_MARKER, BackgroundActivity.TOP_FIVE);
                startActivity(intent);
                break;
            case R.id.user_name:
                mUserName.setVisibility(View.GONE);
                mEditUserName.setText(SharePrefereUtils.getUserName(this));
                mEditUserName.setVisibility(View.VISIBLE);
                mEditUserName.setCursorVisible(true);
                mEditUserName.requestFocus();
                KidsZoneUtil.ShowInputMethod(this);
                KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), false);
                break;
            case R.id.setting_user_img:
                startActivityForResult(new Intent(this, ChooseUserPhoto.class), 3);
                break;
            case R.id.birthday:
                String userBirthday = SharePrefereUtils.getUserBirthday(this);
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onClick:userBirthday ==> " + userBirthday);
                Date date = KidsZoneUtil.StringToDateFormat(userBirthday, KidsZoneUtil.DATE_FORMAT);
                showCalendar(date);
                break;
            default:
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "Click not to do");
                break;
        }
    }

    private void showCalendar(Date defaultVal) {
        if (null == defaultVal)
            defaultVal = new Date(System.currentTimeMillis());
        //modify by wanghong for bug 54177 begin
        DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener, KidsZoneUtil.getYear(defaultVal), KidsZoneUtil.getMonth(defaultVal), KidsZoneUtil.getDay(defaultVal));
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(true);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(true);
        //modify by wanghong for bug 54177 end
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            String birthday = KidsZoneUtil.Date2String(calendar.getTime());
            mBirthday.setText(birthday);
            SharePrefereUtils.saveUserBirthday(ParentalControlMode.this, birthday);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 3:
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(uri));
                        mUserImg.setImageBitmap(KidsZoneUtil.createBitmap(getApplicationContext(), bitmap));
                    } catch (FileNotFoundException e) {
                        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onActivityResult: e" + Log.getStackTraceString(e));
                    } catch (Exception e) {
                        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onActivityResult: e" + Log.getStackTraceString(e));
                    }
                    break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (KidsZoneUtil.isShouldHideInput(view, ev)) {
                ((EditText) view).setCursorVisible(false);
                onFocusChange(view, false);
                KidsZoneUtil.hideInputMethod(this, view);
            } else {
                if (view instanceof EditText) {
                    ((EditText) view).setCursorVisible(true);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.edit_user_name && !hasFocus) {
            String name = mEditUserName.getText().toString();
            if (!TextUtils.isEmpty(name)) {
                SharePrefereUtils.saveUserName(this, name);
                mUserName.setText(name);
            } else {
                Toast.makeText(ParentalControlMode.this, getString(R.string.user_name_not_null), Toast.LENGTH_SHORT).show();
            }
            mEditUserName.setVisibility(View.GONE);
            mUserName.setVisibility(View.VISIBLE);
        }
    }

    private void showCloseDialog() {
        ExitDialog exitDialog = new ExitDialog(this);
        exitDialog.setOnExitOkListener(new ExitDialog.OnExitOkListener() {
            @Override
            public void onOkClick(View v) {
                ((KidsZoneApplication) getApplication()).closeAplication();
            }
        });
        exitDialog.show();
    }

}
