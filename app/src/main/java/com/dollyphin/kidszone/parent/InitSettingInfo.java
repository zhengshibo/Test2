/*Top Secret*/
package com.dollyphin.kidszone.parent;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.util.KidsZoneLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hong.wang on 2016/11/28.
 */
public class InitSettingInfo {
    public static final String xmlns = "http://schemas.android.com/com.dollyphin.kidszone";
    private static final String TAG_SETTING = "Setting";
    private static final String TAG_SETTING_ITEM = "SettingItem";

    private static final String PARAM_LABEL = "label";
    private static final String PARAM_RIGHT_PART = "right_part";
    private static final String PARAM_IS_MODEL = "isModel";
    private static final String PARAM_CLASSNAME = "setting_className";
    private static final String PARAM_PACKAGENAME = "setting_packageName";
    private static final String SKIP_MARKER = "skip_marke";
    private static final String VAULUE_KEY = "value_key";

    public static List<SettingItem> readSettingXml(Context context) {
        List<SettingItem> items = new ArrayList<>();
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "start");
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.setting);
            int eventType = parser.next();
            SettingItem item = null;
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlResourceParser.START_TAG:
                        if (TAG_SETTING.equals(parser.getName())) {
                            KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "start read setting.xml");
                        } else if (TAG_SETTING_ITEM.equals(parser.getName())) {
                            item = new SettingItem();
                            item.setLabel(parser.getAttributeValue(xmlns, PARAM_LABEL));
                            item.setmRightPart(parser.getAttributeValue(xmlns, PARAM_RIGHT_PART));
                            item.setIsModel(parser.getAttributeIntValue(xmlns, PARAM_IS_MODEL, 0));
                            item.setSettingClassName(parser.getAttributeValue(xmlns, PARAM_CLASSNAME));
                            item.setSettingPackageName(parser.getAttributeValue(xmlns, PARAM_PACKAGENAME));
                            item.setSkipMarke(parser.getAttributeIntValue(xmlns, SKIP_MARKER, -1));
                            item.setValueKey(parser.getAttributeValue(xmlns, VAULUE_KEY));
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        if (TAG_SETTING.equals(parser.getName())) {

                        } else if (TAG_SETTING_ITEM.equals(parser.getName())) {
                            items.add(item);
                            item = null;
                        }

                        break;
                    case XmlResourceParser.END_DOCUMENT:

                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, items.toString());
        return items;
    }

}
