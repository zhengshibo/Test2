/*Top Secret*/
package com.dollyphin.kidszone.guide;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;
import com.dollyphin.kidszone.view.UserNameEditTextView;

/**
 * Created by feng.shen on 2016/12/6.
 */

public class Information extends RelativeLayout implements View.OnClickListener, UserNameEditTextView.BackKeyListener {
    private ImageView mUserIcon;
    private TextView mSelect;
    private UserNameEditTextView mUserName;
    private TextView mUserBirth;
    private InformationResult mResult;
    private Context mContext;
    private TextView mUserNameShow;
    private SpannableString mUserNameHint;
    private SpannableString mBitrhHint;

    private String sName = "";
    private String sBitrh = "";

    public interface InformationResult {
        void show(String date);

        void empty(boolean empty);
    }

    public Information(Context context) {
        this(context, null);
    }

    public Information(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Information(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mUserNameHint = new SpannableString(context.getResources().getString(R.string.user_name_hint));
        mBitrhHint = new SpannableString(context.getResources().getString(R.string.user_birth_hint));
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan((int) context.getResources().getDimension(R.dimen.hint_textsize), true);

        mUserNameHint.setSpan(ass, 0, mUserNameHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBitrhHint.setSpan(ass, 0, mBitrhHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void setup(InformationResult result) {
        mResult = result;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            onUpdate();
            //KidsZoneUtil.disableStatusBar(mContext,KidsZoneUtil.DISABLE_NAV); //delete by shenfeng for bug 54282
        } else {
            String name = mUserName.getEditableText().toString();
            //delete by wanghong for bug 54288 begin
//            mUserName.setVisibility(INVISIBLE);
//            mUserNameShow.setVisibility(VISIBLE);
            //delete by wanghong for bug 54288 begin
            mUserNameShow.setText(name);
            if (TextUtils.isEmpty(name)) {
                mUserNameShow.setText("");
                mUserNameShow.setHint(mUserNameHint);
            }
            //KidsZoneUtil.hideNavInKidsZone(mContext, true);
            check();
        }
    }

    @Override
    public boolean onBackKey() {
        String name = mUserName.getEditableText().toString();
        mUserName.setVisibility(INVISIBLE);
        mUserNameShow.setVisibility(VISIBLE);
        mUserNameShow.setText(name);
        if (TextUtils.isEmpty(name)) {
            mUserNameShow.setText("");
            mUserNameShow.setHint(mUserNameHint);
        }
        KidsZoneUtil.hideNavInKidsZone(mContext, true);
        check();
        return false;
    }

    private void init() {
        KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "init==>" + sName);
        mUserIcon = (ImageView) findViewById(R.id.user_icon);
        mSelect = (TextView) findViewById(R.id.select_icon);
        mUserName = (UserNameEditTextView) findViewById(R.id.edit_name);
        mUserBirth = (TextView) findViewById(R.id.edit_birth);
        mUserNameShow = (TextView) findViewById(R.id.user_name_show);

        mUserNameShow.setHint(mUserNameHint);
        mUserBirth.setHint(mBitrhHint);

        mSelect.setOnClickListener(this);
        mUserName.addCallbacks(this);
        mUserName.addTextChangedListener(mUserNameListener);
        mUserName.setOnEditorActionListener(mUserNameEditorListener);
        mUserName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mUserName.setInputType(InputType.TYPE_CLASS_TEXT);
        mUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        mUserName.setOnClickListener(this);
        mUserBirth.setOnClickListener(this);
        mUserNameShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_icon:
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.dollyphin.kidszone", "com.dollyphin.kidszone.util.ChooseUserPhoto");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                KidsZoneUtil.disableStatusBar(mContext, KidsZoneUtil.DISABLE_RECNETS);
                mContext.startActivity(intent);
                break;
            case R.id.edit_birth:
                if (mResult != null) {
                    mResult.show(sBitrh);
                }
                break;
            case R.id.user_name_show:
                mUserNameShow.setVisibility(INVISIBLE);
                mUserName.setVisibility(VISIBLE);
                mUserName.setText(mUserNameShow.getText());
                mUserName.requestFocus();
                mUserName.setSelection(mUserName.length());
                KidsZoneUtil.ShowInputMethod(mContext);
                KidsZoneUtil.hideNavInKidsZone(mContext, false);
                break;
            //add by wanghong for bug 54288 begin
            case R.id.edit_name:
                KidsZoneLog.d(true, "onClick: click");
                mUserName.setCursorVisible(true);
                mUserName.requestFocus();
                KidsZoneUtil.hideNavInKidsZone(mContext, false);
                break;
            //add by wanghong for bug 54288 end
        }
    }

    private void onUpdate() {
        if (mUserIcon != null) {
            mUserIcon.setImageBitmap(KidsZoneUtil.createBitmap(mContext, KidsZoneUtil.getHeadImg()));
        }
    }

    private boolean checkCompleted() {
        boolean birth = checkBirth();
        boolean name = checkName();
        return birth || name;
    }

    private boolean checkName() {
        KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "checkName==>" + sName);
        return TextUtils.isEmpty(sName);
    }

    private boolean checkBirth() {
        KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "checkBirth==>" + sBitrh);
        return TextUtils.isEmpty(sBitrh);
    }

    private TextWatcher mUserNameListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            sName = s.toString();
        }
    };

    private TextView.OnEditorActionListener mUserNameEditorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "OnEditorActionListener actionId==>" + actionId);
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String name = mUserName.getEditableText().toString();
                mUserName.setVisibility(INVISIBLE);
                mUserNameShow.setVisibility(VISIBLE);
                mUserNameShow.setText(name);
                if (TextUtils.isEmpty(name)) {
                    mUserNameShow.setText("");
                    mUserNameShow.setHint(mUserNameHint);
                }
                KidsZoneUtil.hideNavInKidsZone(mContext, true);
                check();
                return false;
            }
            return true;
        }
    };

    public void check() {
        if (mResult != null) {
            mResult.empty(checkCompleted());
        }
    }

    public void syncBirth(String date) {
        KidsZoneLog.d(KidsZoneLog.KIDS_GUIDE_DEBUG, "syncBirth==>" + date);
        mUserBirth.setText(date);
        sBitrh = date;
        check();
    }

    public void saveInformation() {
        SharePrefereUtils.saveUserName(mContext, sName);
        SharePrefereUtils.saveUserBirthday(mContext, sBitrh);
    }
}
