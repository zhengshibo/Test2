/*Top Secret*/
package com.dollyphin.kidszone.parent;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;

import com.dollyphin.kidszone.util.KidsZoneLog;

/**
 * Created by hong.wang on 2016/11/28.
 */
public class SettingItem {

    private String mLabel;
    private String mRightPart;
    private int isModel = 0;
    private String mSettingClassName;
    private String mSettingPackageName;
    private String mValueKey;
    private int mSkipMarke = -1;

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public String getRightPart() {
        return mRightPart;
    }

    public void setmRightPart(String mRightPart) {
        this.mRightPart = mRightPart;
    }

    public String getmSettingClassName() {
        return mSettingClassName;
    }

    public int getIsModel() {
        return isModel;
    }

    public void setIsModel(int isModel) {
        this.isModel = isModel;
    }

    public void setSettingClassName(String mSettingClassName) {
        this.mSettingClassName = mSettingClassName;
    }

    public String getmSettingPackageName() {
        return mSettingPackageName;
    }

    public void setSettingPackageName(String mSettingPackageName) {
        this.mSettingPackageName = mSettingPackageName;
    }

    public void setSkipMarke(int mSkipMarke) {
        this.mSkipMarke = mSkipMarke;
    }

    public Intent getIntent() {
        if (TextUtils.isEmpty(mSettingClassName) || TextUtils.isEmpty(mSettingPackageName)) {
            KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "getIntent:mSettingClassName ==> " + mSettingClassName + "  mSettingPackageName ==> " + mSettingPackageName);
            return null;
        }
        Intent intent = new Intent().setComponent(new ComponentName(mSettingPackageName, mSettingClassName));
        intent.putExtra(BackgroundActivity.SKIP_MARKER, mSkipMarke);
        return intent;
    }

    public String getValueKey() {
        return mValueKey;
    }

    public void setValueKey(String valueKey) {
        this.mValueKey = valueKey;
    }

    @Override
    public String toString() {
        return "SettingItem{" +
                "mLabel='" + mLabel + '\'' +
                ", mRightPart='" + mRightPart + '\'' +
                ", isModel=" + isModel +
                ", mSettingClassName='" + mSettingClassName + '\'' +
                ", mSettingPackageName='" + mSettingPackageName + '\'' +
                ", mValueKey='" + mValueKey + '\'' +
                ", mSkipMarke=" + mSkipMarke +
                '}';
    }
}
