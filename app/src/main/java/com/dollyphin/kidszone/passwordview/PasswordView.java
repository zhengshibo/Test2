/*Top Secret*/
package com.dollyphin.kidszone.passwordview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.SharePrefereUtils;

/**
 * Created by shibo.zheng on 2016/11/30.
 */
public class PasswordView extends LinearLayout implements PasswordBox.onPassWordListener, NumKeyboardView.KeyboardListener, View.OnClickListener {

    private final int COLOR_WHITE = 1;
    private final int INPUT_PWD_ONE = 1;
    private final int INPUT_PWD_TWO = 2;
    private int mInputState = INPUT_PWD_ONE;

    public static final int LOGIN_VIEW = 3;
    public static final int CHECK_VIEW = 4;
    public static final int UPDATE_VIEW = 5;
    private int mState = CHECK_VIEW;
    private boolean isOkPwd = false;

    private int count = 0;

    private String firstPassword;
    private String secondPassword;

    private TextView mPrompt;
    private TextView mForgetPwdText;
    private PasswordBox mPassword;
    private NumKeyboardView mKeyBoard;
    private ForgetPassword mForgetPwd;
    private RelativeLayout mShowPassword;
    private FrameLayout mForgetPassword;
    private ImageView mForget_icon;

    private Context mContext;
    private float mKeyBoardWidth;
    private float mKeyBoardHeight;
    private float mPasswordBoxHeight;
    private float mSpaceItem;
    private int mColorStyle;
    private Drawable mPasswordBoxBg;
    private PassWordComplete mListener;
    private Handler mHandler = new Handler();
    private int CALLBACK_DELAY = 300;

    public interface PassWordComplete {
        void onComplete(boolean complete);

        void onShowPwd(PasswordView view, Spanned s);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PasswordView);
        mKeyBoardWidth = a.getDimension(R.styleable.PasswordView_keyBoard_Width, 100);
        mKeyBoardHeight = a.getDimension(R.styleable.PasswordView_keyBoard_height, 100);
        mPasswordBoxHeight = a.getDimension(R.styleable.PasswordView_password_height, 100);
        mSpaceItem = a.getDimension(R.styleable.PasswordView_space_item, 100);
        mPasswordBoxBg = a.getDrawable(R.styleable.PasswordView_password_background);
        mColorStyle = a.getInt(R.styleable.PasswordView_password_colorStyle, -1);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPassword = (PasswordBox) findViewById(R.id.password);
        mKeyBoard = (NumKeyboardView) findViewById(R.id.numericKeyboard);
        mForgetPwd = (ForgetPassword) findViewById(R.id.forgetPwd);
        mPrompt = (TextView) findViewById(R.id.prompt);

        mForgetPwdText = (TextView) findViewById(R.id.forgetPwdText);
        mShowPassword = (RelativeLayout) findViewById(R.id.showPassword);
        mForgetPassword = (FrameLayout) findViewById(R.id.forgetPassword);

        mForget_icon = (ImageView) findViewById(R.id.forget_icon);

        if (mColorStyle == COLOR_WHITE) {
            mPassword.setTextColor(getResources().getColor(R.color.white));
            mKeyBoard.setColorStyle(mColorStyle);
        }

        if (mPasswordBoxBg != null) {
            mPassword.setPasswordBg(mPasswordBoxBg);
        }
        mPassword.setListener(this);
        mKeyBoard.setKeyBoardListener(this);
        mForgetPwdText.setOnClickListener(this);
        mForgetPwdText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mForgetPwdText.getPaint().setAntiAlias(true);
    }

    @Override
    public void onClick(View view) {
        mPassword.setVisibility(View.INVISIBLE);
        mForgetPwdText.setVisibility(View.INVISIBLE);
        mForgetPassword.setVisibility(View.VISIBLE);
        mKeyBoard.init(0);
        mForget_icon.setImageDrawable(mForgetPwd.getCode(mForgetPwd.getCodeIndex()));
        mPrompt.setText(R.string.input_answer);
        clear();
    }

    @Override
    public void onClickNum(String s) {
        if (mForgetPassword.getVisibility() == VISIBLE) {
            mForgetPwd.addText(s);
        } else {
            mPassword.addText(s);
        }
    }

    @Override
    public void onClear() {
//        clear();
        mPassword.Backspace();
        mForgetPwd.setText("");
    }

    @Override
    public void onClickOk() {
        if (mForgetPwd.isOkAnswer()) {
            String password = SharePrefereUtils.getPassword(getContext());
            String pwd = getResources().getString(R.string.new_password, password);

            if (mListener != null) {
                mListener.onShowPwd(this,  Html.fromHtml(pwd));
            }
            isOkPwd = true;
        } else {
            mPrompt.setText(R.string.answer_error);
        }
        clear();
    }

    @Override
    public void onComplete(String s) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clear();
            }
        }, CALLBACK_DELAY);
        changePwdView(s);
    }

    private void clear() {
        mPassword.setText("");
        mForgetPwd.setText("");
    }

    private boolean isPassword(String s) {
        String oldPassword = SharePrefereUtils.getPassword(getContext());

        if (!s.equals(oldPassword)) {
            mPrompt.setText(R.string.password_error_input);
            count++;
            if (count >= 5) {
                mForgetPwdText.setVisibility(View.VISIBLE);
            }
            return false;
        }
        mPrompt.setText(R.string.input_password);
        mForgetPwdText.setVisibility(View.INVISIBLE);
        count = 0;
        return true;
    }

    private void changePwdView(String s) {
        switch (mState) {
            case CHECK_VIEW:
                boolean isRight = isPassword(s);
                callbackDelay(isRight);
                break;
            case LOGIN_VIEW:
                mPrompt.setText(R.string.input_password);
                inputPwd(s);
                break;
            case UPDATE_VIEW:
                if (!isOkPwd) {
                    isOkPwd = isPassword(s);
                } else {
                    isOkPwd = true;
                    inputPwd(s);
                }
                break;
        }
    }

    private boolean inputPwd(String s) {
        boolean flag = false;
        mPrompt.setText(R.string.input_password);
        switch (mInputState) {
            case INPUT_PWD_ONE:
                firstPassword = s;
                mInputState = INPUT_PWD_TWO;
                flag = true;
                mPrompt.setText(R.string.again_input_password);
                break;
            case INPUT_PWD_TWO:
                secondPassword = s;
                if (firstPassword.equals(secondPassword)) {
                    SharePrefereUtils.setPassword(getContext(), secondPassword);
                    flag = true;
                    mPrompt.setText(R.string.password_success);
                    callbackDelay(true);
                } else {
                    flag = false;
                    mPrompt.setText(R.string.password_error);
                }
                firstPassword = "";
                secondPassword = "";
                mInputState = INPUT_PWD_ONE;
                break;
        }
        return flag;
    }

    public void setup(int state, PassWordComplete listener) {
        mState = state;
        mListener = listener;
        initView();
    }

    public void setup(int state) {
        mState = state;
        initView();
    }

    public void initView() {
        mPassword.setText("");
        mPrompt.setText(R.string.input_password);
        mInputState = INPUT_PWD_ONE;
        mForgetPassword.setVisibility(INVISIBLE);
        mPassword.setVisibility(VISIBLE);
        mKeyBoard.init(-1);
        count = 0;
    }

    private void callbackDelay(final boolean complete) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onComplete(complete);
                    mPrompt.setText(R.string.input_password);
                }
            }
        }, CALLBACK_DELAY);
    }
}
