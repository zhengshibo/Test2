/*Top Secret*/
package com.dollyphin.kidszone.app;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import com.dollyphin.kidszone.util.KidsZoneLog;
import com.dollyphin.kidszone.util.KidsZoneUtil;
import com.dollyphin.kidszone.util.SharePrefereUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by feng.shen on 2017/1/13.
 */

public class LocaleService extends Service {

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            double start = System.currentTimeMillis();
            PackageManager pm = getApplicationContext().getPackageManager();
            List<ResolveInfo> infos = getAllAppList(pm);
            Collections.sort(infos, new ResolveInfo.DisplayNameComparator(pm));
            SharePrefereUtils.clearAllAppsSort(getApplicationContext());
            int i = 0;
            for (ResolveInfo info : infos) {
                KidsZoneLog.d(KidsZoneLog.KIDS_LANGUAGE_DEBUG, "LocaleBinder app==>" + (info.activityInfo.name) + "==i==" + i);
                SharePrefereUtils.addAllAppsSort(getApplicationContext(), KidsZoneUtil.getApp(info.activityInfo.packageName, info.activityInfo.name), i);
                i++;
            }
            KidsZoneLog.d(KidsZoneLog.KIDS_LANGUAGE_DEBUG, "LocaleBinder time==>" + (System.currentTimeMillis() - start));
            stopSelf();
        }
    });

    private List<ResolveInfo> getAllAppList(PackageManager pm) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return pm.queryIntentActivities(intent, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread.run();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
