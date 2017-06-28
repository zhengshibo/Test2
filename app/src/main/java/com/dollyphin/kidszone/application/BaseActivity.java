/*Top Secret*/
package com.dollyphin.kidszone.application;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.lockscreen.LockScreenActivity;
import com.dollyphin.kidszone.lockscreen.LockScreenManager;
import com.dollyphin.kidszone.startpage.StartPage;
import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by feng.shen on 2016/11/23.
 */

public class BaseActivity extends Activity {
    private List<String> mNeedPermissions = new ArrayList<>();
    protected final int REQUEST_PERMISSION = 10001;
    private final int REQUEST_SETTING_PERMISSION = 10002;
    protected String[] mPermissions;
    boolean isTop = true;
    private static boolean isActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        if ("P904".equals(getString(R.string.project))) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        KidsZoneApplication app = (KidsZoneApplication) getApplication();
        app.addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!(this instanceof LockScreenActivity || this instanceof StartPage)) {
            if (!isActive) {
                isActive = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        KidsZoneUtil.hideNav(getWindow().getDecorView());
        KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), false);
/*        if (!isActive && (this instanceof LockScreenActivity)) {
            if (LockScreenManager.lockState == LockScreenManager.LOCK) {
                KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), true);
            } else {
                KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), false);
            }

        } else */if (isActive && (this instanceof LockScreenActivity)) {
            KidsZoneUtil.hideNavInKidsZone(getApplicationContext(), true);
        }
        if (KidsZoneUtil.getKidsZoneLockedHide(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!(this instanceof LockScreenActivity || this instanceof StartPage)) {
            isActive = KidsZoneUtil.isForeground(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        KidsZoneUtil.hideNav(getWindow().getDecorView());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            KidsZoneUtil.hideNav(getWindow().getDecorView());
        }
        super.onWindowFocusChanged(hasFocus);
    }

    private static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void checkPermissions(String... permissions) {
        KidsZoneLog.d(KidsZoneLog.KIDS_PERMISSION_DEBUG, "checkPermissions: permissions  == " + Arrays.toString(permissions));
        if (!isTop) {
            return;
        }
        mNeedPermissions.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    mNeedPermissions.add(permission);
                }
            }

            KidsZoneLog.d(KidsZoneLog.KIDS_PERMISSION_DEBUG, "checkPermissions: mNeedPermissions  == " + Arrays.toString(mNeedPermissions.toArray()));
            if (mNeedPermissions.size() > 0) {
                requestPermissions(mNeedPermissions.toArray(new String[mNeedPermissions.size()]), REQUEST_PERMISSION);
            }
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.permissions_help))
                .setMessage(getString(R.string.permissions_msg))
                .setPositiveButton(getString(R.string.permissions_setting), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_SETTING_PERMISSION);
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
    }


    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mNeedPermissions.clear();
        if (requestCode == REQUEST_PERMISSION) {
            int count = grantResults.length;

            for (int i = 0; i < count; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //the permission has been granted to the given package
                } else {
                    mNeedPermissions.add(permissions[i]);
                    isTop = shouldShowRequestPermissionRationale(permissions[i]);
                }
            }
            if (!isTop) {
                showDialog();
            } else if (mNeedPermissions.size() > 0) {
                KidsZoneLog.d(KidsZoneLog.KIDS_PERMISSION_DEBUG, "Permission request failed:  " + mNeedPermissions.toString());
                onFailedRequestPermissions(mNeedPermissions.toArray(new String[mNeedPermissions.size()]));
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTING_PERMISSION) {
            isTop = true;
            checkPermissions(mNeedPermissions.toArray(new String[mNeedPermissions.size()]));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onFailedRequestPermissions(String... permissions) {

    }
}
