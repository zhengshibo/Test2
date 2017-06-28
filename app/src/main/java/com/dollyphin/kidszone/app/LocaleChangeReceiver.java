/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by feng.shen on 2017/1/9.
 */

public class LocaleChangeReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*String language = context.getResources().getConfiguration().locale.getLanguage().toString();
        String current = SharePrefereUtils.getLanguage(context);
        KidsZoneLog.d(KidsZoneLog.KIDS_LANGUAGE_DEBUG, "LocaleBinder language==>" + language + "==c==" + current);
        if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED) || ACTION_BOOT.equals(intent.getAction())) {
            if (!current.equals(language)) {
                SharePrefereUtils.setLocaleLanguage(context, language);
                Intent serviceIntent = new Intent(context, LocaleService.class);
                context.startService(serviceIntent);
            }
        }*/
    }
}
